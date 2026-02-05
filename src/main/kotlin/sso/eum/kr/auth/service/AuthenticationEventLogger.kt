package sso.eum.kr.auth.service

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component

@Component
class AuthenticationEventLogger {

    private val logger = LoggerFactory.getLogger(AuthenticationEventLogger::class.java)

    @EventListener
    fun handleAuthenticationSuccess(event: AuthenticationSuccessEvent) {
        val username = event.authentication.name
        logger.info("Login Success: User '{}' successfully authenticated.", username)
    }

    @EventListener
    fun handleAuthenticationFailure(event: AuthenticationFailureBadCredentialsEvent) {
        val username = event.authentication.principal
        logger.warn("Login Failure: Bad credentials for user '{}'.", username)
    }
}