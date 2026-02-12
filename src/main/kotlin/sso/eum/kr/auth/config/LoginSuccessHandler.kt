package sso.eum.kr.auth.config

import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler
import org.springframework.stereotype.Component

/**
 * 로그인 성공 후 처리를 담당하는 핸들러.
 *
 * Spring Security의 기본 동작을 활용하여, 다음과 같이 작동합니다:
 * 1. 인증이 필요하여 로그인 페이지로 리다이렉트된 경우 (예: OIDC 인증 흐름):
 *    로그인 성공 후, 원래 가려던 목적지(저장된 요청)로 사용자를 되돌려 보냅니다.
 * 2. 사용자가 직접 로그인 페이지로 와서 로그인한 경우:
 *    설정된 기본 URL(defaultTargetUrl)로 리다이렉트합니다.
 */
@Component
class LoginSuccessHandler : SavedRequestAwareAuthenticationSuccessHandler() {
    init {
        // 사용자가 직접 로그인했을 때 이동할 기본 URL을 설정합니다.
        defaultTargetUrl = "/"
    }
}