package sso.eum.kr.auth.controller

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import sso.eum.kr.auth.service.UserService
import sso.eum.kr.auth.service.dto.UserRegistrationRequest
import sso.eum.kr.auth.service.dto.UserResponse
import sso.eum.kr.auth.service.dto.UserUpdateRequest
import java.net.URI

@Controller
class UserWebController(private val userService: UserService) {

    private val logger = LoggerFactory.getLogger(UserWebController::class.java)

    @PostMapping("/register")
    fun registerUserFromForm(
        @ModelAttribute("userRegistrationRequest") request: UserRegistrationRequest,
        redirectAttributes: RedirectAttributes,
        model: Model
    ): String {
        return try {
            userService.registerUser(request) // 반환값을 사용하지 않으므로 타입 불일치 문제 없음
            redirectAttributes.addFlashAttribute("registrationSuccess", true)
            "redirect:/login"
        } catch (e: IllegalStateException) {
            model.addAttribute("error", e.message)
            "register"
        } catch (e: Exception) {
            logger.error("Web form user registration failed", e)
            model.addAttribute("error", "An unexpected error occurred during registration. Please try again later.")
            "register"
        }
    }
}

@RestController
@RequestMapping("/api/v1/users")
class UserApiController(private val userService: UserService) {

    private val logger = LoggerFactory.getLogger(UserApiController::class.java)

    @PostMapping("/register")
    fun registerUser(@RequestBody request: UserRegistrationRequest): ResponseEntity<*> {
        val userResponse = userService.registerUser(request) // UserResponse를 받음
        return ResponseEntity.created(URI.create("/api/v1/users/${userResponse.userId}")).body(
            mapOf("message" to "User registered successfully", "userId" to userResponse.userId)
        )
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponse>> {
        val users = userService.findAllUsers()
        return ResponseEntity.ok(users)
    }

    @GetMapping("/{userId}")
    fun getUserByUserId(@PathVariable userId: String): ResponseEntity<UserResponse> {
        val user = userService.findUserByUserId(userId)
        return ResponseEntity.ok(user)
    }

    @PutMapping("/{userId}")
    fun updateUser(@PathVariable userId: String, @RequestBody request: UserUpdateRequest): ResponseEntity<UserResponse> {
        val updatedUser = userService.updateUser(userId, request)
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/{userId}")
    fun deleteUser(@PathVariable userId: String): ResponseEntity<Void> {
        userService.deleteUser(userId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{userId}/roles")
    fun addRoleToUser(@PathVariable userId: String, @RequestBody roleRequest: Map<String, String>): ResponseEntity<UserResponse> {
        val roleName = roleRequest["roleName"] ?: throw IllegalArgumentException("roleName must be provided")
        val updatedUser = userService.addRoleToUser(userId, roleName)
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/{userId}/roles")
    fun removeRoleFromUser(@PathVariable userId: String, @RequestBody roleRequest: Map<String, String>): ResponseEntity<UserResponse> {
        val roleName = roleRequest["roleName"] ?: throw IllegalArgumentException("roleName must be provided")
        val updatedUser = userService.removeRoleFromUser(userId, roleName)
        return ResponseEntity.ok(updatedUser)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUserNotFound(ex: UsernameNotFoundException): ResponseEntity<Map<String, String>> {
        logger.warn(ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "User not found")))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFound(ex: EntityNotFoundException): ResponseEntity<Map<String, String>> {
        logger.warn(ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Entity not found")))
    }

    @ExceptionHandler(IllegalStateException::class, IllegalArgumentException::class)
    fun handleIllegalState(ex: RuntimeException): ResponseEntity<Map<String, String>> {
        logger.warn(ex.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (ex.message ?: "Invalid request")))
    }
}