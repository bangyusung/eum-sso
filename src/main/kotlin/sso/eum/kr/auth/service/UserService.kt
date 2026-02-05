package sso.eum.kr.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.User
import sso.eum.kr.auth.repository.RoleRepository
import sso.eum.kr.auth.repository.UserRepository
import sso.eum.kr.auth.service.dto.UserRegistrationRequest

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun registerUser(request: UserRegistrationRequest): User {
        if (userRepository.existsByUserId(request.userId)) {
            throw IllegalStateException("User ID '${request.userId}' is already taken.")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalStateException("Email '${request.email}' is already in use.")
        }

        val userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow { IllegalStateException("Default role 'ROLE_USER' not found.") }

        val user = User(
            userId = request.userId,
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            roles = mutableSetOf(userRole)
        )

        return userRepository.save(user)
    }
}