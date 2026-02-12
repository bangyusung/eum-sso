package sso.eum.kr.auth.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.User
import sso.eum.kr.auth.repository.OrgRepository
import sso.eum.kr.auth.repository.RoleRepository
import sso.eum.kr.auth.repository.UserRepository
import sso.eum.kr.auth.service.dto.UserRegistrationRequest
import sso.eum.kr.auth.service.dto.UserResponse
import sso.eum.kr.auth.service.dto.UserUpdateRequest

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val orgRepository: OrgRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun registerUser(request: UserRegistrationRequest): UserResponse {
        if (userRepository.existsByUserId(request.userId)) {
            throw IllegalStateException("User ID '${request.userId}' is already taken.")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalStateException("Email '${request.email}' is already in use.")
        }

        val organization = request.orgId?.let {
            orgRepository.findById(it).orElseThrow { EntityNotFoundException("Organization not found with id: ${request.orgId}") }
        }

        val roles = request.roles.map { roleName ->
            roleRepository.findByName(roleName)
                .orElseThrow { EntityNotFoundException("Role not found with name: $roleName") }
        }.toMutableSet()

        val user = User(
            userId = request.userId,
            username = request.username,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            organization = organization,
            deptName = request.deptName,
            phoneNumber = request.phoneNumber,
            userRole = request.userRole ?: "STAFF",
            roles = roles
        )

        val savedUser = userRepository.save(user)
        return UserResponse.from(savedUser)
    }

    fun findAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { UserResponse.from(it) }
    }

    fun findUserByUserId(userId: String): UserResponse {
        val user = userRepository.findByUserId(userId)
            .orElseThrow { UsernameNotFoundException("User not found with user_id: $userId") }
        return UserResponse.from(user)
    }

    @Transactional
    fun updateUser(userId: String, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findByUserId(userId)
            .orElseThrow { UsernameNotFoundException("User not found with user_id: $userId") }

        request.username?.let { user.username = it }
        request.email?.let {
            if (user.email != it && userRepository.existsByEmail(it)) {
                throw IllegalStateException("Email '$it' is already in use.")
            }
            user.email = it
        }
        request.enabled?.let { user.enabled = it }
        request.orgId?.let {
            user.organization = orgRepository.findById(it).orElseThrow { EntityNotFoundException("Organization not found with id: $it") }
        }
        request.deptName?.let { user.deptName = it }
        request.phoneNumber?.let { user.phoneNumber = it }
        request.userRole?.let { user.userRole = it }

        val updatedUser = userRepository.save(user)
        return UserResponse.from(updatedUser)
    }

    @Transactional
    fun deleteUser(userId: String) {
        val user = userRepository.findByUserId(userId)
            .orElseThrow { UsernameNotFoundException("User not found with user_id: $userId") }
        user.deleted = true
        userRepository.save(user)
    }

    @Transactional
    fun addRoleToUser(userId: String, roleName: String): UserResponse {
        val user = userRepository.findByUserId(userId)
            .orElseThrow { UsernameNotFoundException("User not found with user_id: $userId") }
        val role = roleRepository.findByName(roleName)
            .orElseThrow { EntityNotFoundException("Role not found with name: $roleName") }

        user.roles.add(role)
        return UserResponse.from(userRepository.save(user))
    }

    @Transactional
    fun removeRoleFromUser(userId: String, roleName: String): UserResponse {
        val user = userRepository.findByUserId(userId)
            .orElseThrow { UsernameNotFoundException("User not found with user_id: $userId") }
        val role = roleRepository.findByName(roleName)
            .orElseThrow { EntityNotFoundException("Role not found with name: $roleName") }

        user.roles.remove(role)
        return UserResponse.from(userRepository.save(user))
    }
}