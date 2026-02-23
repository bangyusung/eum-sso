package sso.eum.kr.auth.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationEventPublisher
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcLogoutAuthenticationToken
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import sso.eum.kr.auth.domain.User
import sso.eum.kr.auth.repository.UserRepository
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val loginSuccessHandler: LoginSuccessHandler,
    private val loginFailureHandler: LoginFailureHandler,
    private val userRepository: UserRepository // UserRepository 주입
) {

    @Bean
    @Order(1)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc { oidc ->
                oidc.logoutEndpoint { logout ->
                    logout.logoutResponseHandler { request, response, authentication ->
                        val token = authentication as OidcLogoutAuthenticationToken
                        val postLogoutRedirectUri = token.postLogoutRedirectUri
                        val redirectStrategy = DefaultRedirectStrategy()
                        if (postLogoutRedirectUri != null) {
                            redirectStrategy.sendRedirect(request, response, postLogoutRedirectUri)
                        }
                    }
                }
            }

        http
            .exceptionHandling { exceptions ->
                exceptions.authenticationEntryPoint(
                    LoginUrlAuthenticationEntryPoint("/login")
                )
            }
            .oauth2ResourceServer { resourceServer ->
                resourceServer.jwt(Customizer.withDefaults())
            }
        
        http.formLogin(Customizer.withDefaults())

        return http.build()
    }

    @Bean
    @Order(2)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/register", "/v1/users/**", "/login", "/logout-callback").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin {
                it.loginPage("/login")
                  .successHandler(loginSuccessHandler)
                  .failureHandler(loginFailureHandler)
            }
            .csrf { it.ignoringRequestMatchers("/register", "/v1/users/**") }

        return http.build()
    }

    @Bean
    fun tokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer { context ->
            if (context.tokenType.value == OidcParameterNames.ID_TOKEN) {
                val principal: Authentication = context.getPrincipal()
                val user = userRepository.findByAccount(principal.name)
                    .orElseThrow { UsernameNotFoundException("User not found: ${principal.name}") }

                val userClaims = buildUserClaims(user)
                context.claims.claims { claims ->
                    claims.putAll(userClaims)
                }
            }
        }
    }

    private fun buildUserClaims(user: User): Map<String, Any> {
        val org = user.org
        val orgClaims = mapOf(
            "id" to (org.id ?: 0),
            "orgType" to org.orgType,
            "bizName" to org.bizName,
            "bizNumber" to org.bizNumber,
            "repName" to org.repName,
            "address" to org.address,
            "status" to org.status
        )

        return mapOf(
            "user_id" to (user.id ?: 0),
            "username" to user.username,
            "email" to user.email,
            "phone_number" to user.phoneNumber,
            "dept_name" to (user.deptName ?: ""),
            "status" to user.status,
            "organization" to orgClaims
        )
    }

    @Bean
    fun authenticationEventPublisher(applicationEventPublisher: ApplicationEventPublisher): AuthenticationEventPublisher {
        return DefaultAuthenticationEventPublisher(applicationEventPublisher)
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = listOf("http://localhost:3000")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("oidc-client")
            .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri("http://localhost:3000/login-callback")
            .postLogoutRedirectUri("http://localhost:3000/logout-callback")
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .build()
        return InMemoryRegisteredClientRepository(registeredClient)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val keyPair = generateRsaKey()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        val rsaKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet(jwkSet)
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings {
        return AuthorizationServerSettings.builder().build()
    }

    private fun generateRsaKey(): KeyPair {
        val keyPair: KeyPair
        try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            keyPair = keyPairGenerator.generateKeyPair()
        } catch (ex: Exception) {
            throw IllegalStateException(ex)
        }
        return keyPair
    }
}