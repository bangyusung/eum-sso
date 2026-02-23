package sso.eum.kr.auth.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class LoginFailureHandler : SimpleUrlAuthenticationFailureHandler() {

    private val logger = LoggerFactory.getLogger(LoginFailureHandler::class.java)

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val username = request.getParameter("username")
        
        logger.warn("Login failed for user '$username': ${exception.message}")
        logger.debug("Login failure details for user '$username'", exception)

        val errorMessage = when (exception.javaClass.simpleName) {
            "BadCredentialsException" -> "아이디 또는 비밀번호가 맞지 않습니다."
            "UsernameNotFoundException" -> "가입되지 않은 아이디입니다."
            "AccountExpiredException" -> "만료된 계정입니다."
            "CredentialsExpiredException" -> "비밀번호 유효기간이 만료되었습니다."
            "DisabledException" -> "비활성화된 계정입니다. 관리자에게 문의하세요."
            "LockedException" -> "잠긴 계정입니다."
            else -> "로그인 처리 중 알 수 없는 오류가 발생했습니다."
        }

        // URL-encode the error message to safely include it in the query parameter
        val encodedErrorMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString())
        setDefaultFailureUrl("/login?error=true&message=${encodedErrorMessage}")

        super.onAuthenticationFailure(request, response, exception)
    }
}