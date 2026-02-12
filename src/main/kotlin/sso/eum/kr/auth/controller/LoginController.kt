package sso.eum.kr.auth.controller

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import sso.eum.kr.auth.service.OrgService
import sso.eum.kr.auth.service.RoleService
import sso.eum.kr.auth.service.dto.UserRegistrationRequest

@Controller
class LoginController(
    private val orgService: OrgService,
    private val roleService: RoleService
) {

    @GetMapping("/login")
    fun login(authentication: Authentication?): String {
        if (authentication != null && authentication.isAuthenticated) {
            if (authentication.authorities.any { it.authority != "ROLE_ANONYMOUS" }) {
                return "redirect:/"
            }
        }
        return "login"
    }

    @GetMapping("/register")
    fun showRegistrationForm(model: Model, authentication: Authentication?): String {
        if (authentication != null && authentication.isAuthenticated) {
            if (authentication.authorities.any { it.authority != "ROLE_ANONYMOUS" }) {
                return "redirect:/"
            }
        }

        model.addAttribute("userRegistrationRequest", UserRegistrationRequest(userId = "", username = "", email = "", password = ""))
        model.addAttribute("allOrgs", orgService.findAllOrgs())
        model.addAttribute("allRoles", roleService.findAllRoles())
        return "register"
    }
}