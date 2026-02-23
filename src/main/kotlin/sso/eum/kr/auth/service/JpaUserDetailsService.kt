package sso.eum.kr.auth.service

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import sso.eum.kr.auth.repository.UserRepository
import org.springframework.security.core.userdetails.User as SpringUser

@Service
class JpaUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByAccount(username)
            .orElseThrow { UsernameNotFoundException("User not found with account: $username") }

        val authorities = user.roles.map { SimpleGrantedAuthority(it.name) }

        return SpringUser.builder()
            .username(user.account)
            .password(user.password)
            .authorities(authorities)
            .accountExpired(false) // `accountExpired` 필드는 현재 User 모델에 없음
            .accountLocked(user.locked)
            .credentialsExpired(false) // `credentialsExpired` 필드는 현재 User 모델에 없음
            .disabled(user.status != "APPROVED") // `status`가 'APPROVED'가 아니면 비활성화
            .build()
    }
}