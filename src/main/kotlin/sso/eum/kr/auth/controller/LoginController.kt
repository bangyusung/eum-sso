package sso.eum.kr.auth.controller

import jakarta.servlet.http.HttpSession
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import sso.eum.kr.auth.service.UserService
import sso.eum.kr.auth.service.dto.SignUpRequest

@Controller
class LoginController(private val userService: UserService) {

    private val logger = LoggerFactory.getLogger(LoginController::class.java)

    @GetMapping("/login")
    fun login(session: HttpSession, model: Model): String {
        val userId = session.getAttribute("loginUserId")
        val userRoles = session.getAttribute("loginUserRoles")

        if (userId != null && userRoles != null) {
            model.addAttribute("loginUserId", userId)
            model.addAttribute("loginUserRoles", userRoles)
            session.removeAttribute("loginUserId")
            session.removeAttribute("loginUserRoles")
        }

        return "login"
    }

    @GetMapping("/register")
    fun showRegistrationForm(model: Model): String {
        model.addAttribute("signUpRequest", SignUpRequest(
            account = "", username = "", email = "", password = "",
            deptName = null, phoneNumber = "", orgType = "",
            bizName = "", bizNumber = "", repName = "", address = ""
        ))
        return "register"
    }

    @PostMapping("/register")
    fun registerUserFromForm(
        @ModelAttribute("signUpRequest") request: SignUpRequest,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        return try {
            userService.registerUser(request)
            redirectAttributes.addFlashAttribute("registrationSuccess", "true")
            "redirect:/login"
        } catch (e: IllegalStateException) {
            model.addAttribute("error", e.message)
            "register"
        } catch (e: IllegalArgumentException) {
            model.addAttribute("error", e.message)
            "register"
        } catch (e: Exception) {
            logger.error("Web form user registration failed", e)
            model.addAttribute("error", "An unexpected error occurred during registration. Please try again later.")
            "register"
        }
    }

    @GetMapping("/logout-callback")
    fun logoutCallback(): String {
        return "logout-callback"
    }
}