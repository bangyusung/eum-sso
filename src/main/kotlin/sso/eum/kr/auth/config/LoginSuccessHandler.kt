package sso.eum.kr.auth.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class LoginSuccessHandler : SavedRequestAwareAuthenticationSuccessHandler() {

    private val logger = LoggerFactory.getLogger(LoginSuccessHandler::class.java)

    init {
        // 인증 흐름(예: OIDC)이 없는 일반적인 로그인 성공 시 이동할 기본 URL
        defaultTargetUrl = "/"
    }

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // Use Kotlin's string interpolation to avoid logging API conflicts
        logger.info("User '${authentication.name}' logged in successfully. Roles: ${authentication.authorities}")
        
        // SavedRequestAwareAuthenticationSuccessHandler의 로직을 실행하여
        // 인증 시작 시 요청이 저장된 경우(예: OIDC) 해당 URL로 리다이렉트하고,
        // 그렇지 않은 경우 defaultTargetUrl로 리다이렉트합니다.
        super.onAuthenticationSuccess(request, response, authentication)
    }
}