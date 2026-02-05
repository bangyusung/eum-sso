package sso.eum.kr.auth.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import sso.eum.kr.auth.service.UserService
import sso.eum.kr.auth.service.dto.UserRegistrationRequest
import java.net.URI

@Controller
class UserController(private val userService: UserService) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    /**
     * RESTful API for user registration (e.g., for client applications)
     */
    @PostMapping("/api/v1/users/register")
    @ResponseBody
    fun registerUserApi(@RequestBody request: UserRegistrationRequest): ResponseEntity<*> {
        return try {
            val user = userService.registerUser(request)
            ResponseEntity.created(URI.create("/api/v1/users/${user.userId}")).body(
                mapOf("message" to "User registered successfully", "userId" to user.userId)
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("API user registration failed", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "An internal server error occurred."))
        }
    }

    /**
     * Handles web form submission for user registration
     */
    @PostMapping("/register")
    fun registerUserFromForm(
        @ModelAttribute("userRegistrationRequest") request: UserRegistrationRequest,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        return try {
            userService.registerUser(request)
            redirectAttributes.addFlashAttribute("registrationSuccess", true)
            "redirect:/login"
        } catch (e: IllegalStateException) {
            model.addAttribute("error", e.message)
            "register" // Show the registration form again with a specific error message
        } catch (e: Exception) {
            logger.error("Web form user registration failed", e)
            model.addAttribute("error", "An unexpected error occurred during registration. Please try again later.")
            "register" // Show the registration form again with a generic error message
        }
    }
}