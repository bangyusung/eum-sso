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
        val user = userRepository.findByUserId(username)
            .orElseThrow { UsernameNotFoundException("User not found with user_id: $username") }

        val authorities = user.roles.map { SimpleGrantedAuthority(it.name) }

        return SpringUser.builder()
            .username(user.userId)
            .password(user.password)
            .authorities(authorities)
            .accountExpired(!user.accountNonExpired)
            .accountLocked(!user.accountNonLocked)
            .credentialsExpired(!user.credentialsNonExpired)
            .disabled(!user.enabled)
            .build()
    }
}