package sso.eum.kr.auth.controller

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.*
import sso.eum.kr.auth.repository.UserRepository
import sso.eum.kr.auth.service.UserService
import sso.eum.kr.auth.service.dto.SignUpRequest
import sso.eum.kr.auth.service.dto.UserInfoResponse
import java.net.URI
import java.security.Principal

@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userService: UserService,
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/me")
    fun getCurrentUser(principal: Principal): ResponseEntity<*> {
        val user = userRepository.findByAccount(principal.name)
            .orElseThrow { UsernameNotFoundException("User not found with account: ${principal.name}") }

        val response = UserInfoResponse(
            id = user.id!!,
            account = user.account,
            username = user.username,
            email = user.email,
            phoneNumber = user.phoneNumber,
            deptName = user.deptName,
            status = user.status,
            roles = user.roles.map { it.name }.toSet(),
            org = UserInfoResponse.OrgInfo(
                id = user.org.id!!,
                bizName = user.org.bizName,
                orgType = user.org.orgType
            )
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/register")
    fun registerUserApi(@RequestBody request: SignUpRequest): ResponseEntity<*> {
        return try {
            val user = userService.registerUser(request)
            ResponseEntity.created(URI.create("/api/v1/users/${user.id}")).body(
                mapOf("message" to "User registered successfully", "id" to user.id)
            )
        } catch (e: IllegalStateException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            logger.error("API user registration failed", e)
            ResponseEntity.internalServerError().body(mapOf("error" to "An internal server error occurred."))
        }
    }
}