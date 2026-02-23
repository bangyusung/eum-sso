package sso.eum.kr.auth.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.Org
import sso.eum.kr.auth.domain.User
import sso.eum.kr.auth.repository.OrgRepository
import sso.eum.kr.auth.repository.RoleRepository
import sso.eum.kr.auth.repository.UserRepository
import sso.eum.kr.auth.service.dto.SignUpRequest

@Service
class UserService(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val orgRepository: OrgRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun registerUser(request: SignUpRequest): User {
        if (userRepository.existsByAccount(request.account)) {
            throw IllegalStateException("Account '${request.account}' is already taken.")
        }
        if (userRepository.existsByEmail(request.email)) {
            throw IllegalStateException("Email '${request.email}' is already in use.")
        }
        if (orgRepository.existsByBizNumber(request.bizNumber)) {
            throw IllegalStateException("Business number '${request.bizNumber}' is already registered.")
        }

        // 1. Create and save Org
        val newOrg = Org().apply {
            orgType = request.orgType
            bizName = request.bizName
            bizNumber = request.bizNumber
            repName = request.repName
            address = request.address
        }
        val savedOrg = orgRepository.save(newOrg)

        // 2. Create and save User
        val userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow { IllegalStateException("Default role 'ROLE_USER' not found.") }

        val newUser = User().apply {
            account = request.account
            org = savedOrg
            username = request.username
            password = passwordEncoder.encode(request.password)
            email = request.email
            deptName = request.deptName
            phoneNumber = request.phoneNumber
            roles = mutableSetOf(userRole)
        }

        return userRepository.save(newUser)
    }
}