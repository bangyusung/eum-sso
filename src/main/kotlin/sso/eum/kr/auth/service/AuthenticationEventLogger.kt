package sso.eum.kr.auth.service

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent
import org.springframework.security.authentication.event.AuthenticationSuccessEvent
import org.springframework.stereotype.Component

@Component
class AuthenticationEventLogger {

    private val logger = LoggerFactory.getLogger(AuthenticationEventLogger::class.java)

    @EventListener
    fun handleAuthenticationSuccess(event: AuthenticationSuccessEvent) {
        val auth = event.authentication
        logger.info(
            "✅ AUTH SUCCESS: Principal='{}', Type='{}', Details='{}'",
            auth.name,
            auth.javaClass.simpleName,
            auth.details
        )
    }

    @EventListener
    fun handleAuthenticationFailure(event: AbstractAuthenticationFailureEvent) {
        val auth = event.authentication
        val exception = event.exception
        logger.error(
            "❌ AUTH FAILURE: Principal='{}', Type='{}', Exception='{}'",
            auth.name,
            auth.javaClass.simpleName,
            exception.message,
            exception // Pass the full exception to log the stack trace
        )
    }
}