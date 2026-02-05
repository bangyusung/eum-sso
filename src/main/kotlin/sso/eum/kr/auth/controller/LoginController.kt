package sso.eum.kr.auth.controller

import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import sso.eum.kr.auth.service.dto.UserRegistrationRequest

@Controller
class LoginController {

    @GetMapping("/login")
    fun login(session: HttpSession, model: Model): String {
        val userId = session.getAttribute("loginUserId")
        val userRoles = session.getAttribute("loginUserRoles")

        if (userId != null && userRoles != null) {
            model.addAttribute("loginUserId", userId)
            model.addAttribute("loginUserRoles", userRoles)

            // 모델에 추가한 후 세션에서 제거
            session.removeAttribute("loginUserId")
            session.removeAttribute("loginUserRoles")
        }

        return "login"
    }

    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        model.addAttribute("userRegistrationRequest", UserRegistrationRequest("", "", "", ""))
        return "register"
    }
}