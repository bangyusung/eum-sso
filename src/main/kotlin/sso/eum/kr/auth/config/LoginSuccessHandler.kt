package sso.eum.kr.auth.config

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class LoginSuccessHandler : SimpleUrlAuthenticationSuccessHandler() {
    init {
        // 로그인 성공 시 이동할 기본 URL을 설정합니다.
        defaultTargetUrl = "/"
        // 항상 defaultTargetUrl로 리다이렉트하도록 설정합니다.
        // (이전 요청이 있었더라도 무시하고 항상 홈으로 보냅니다.)
        setAlwaysUseDefaultTargetUrl(true)
    }
}