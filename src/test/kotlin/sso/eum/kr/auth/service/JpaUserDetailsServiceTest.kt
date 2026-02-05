package sso.eum.kr.auth.service

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import sso.eum.kr.auth.domain.Role
import sso.eum.kr.auth.domain.User
import sso.eum.kr.auth.repository.UserRepository
import java.util.*

class JpaUserDetailsServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var userDetailsService: JpaUserDetailsService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        userDetailsService = JpaUserDetailsService(userRepository)
    }

    @Test
    @DisplayName("존재하는 user_id로 사용자 정보를 성공적으로 로드해야 한다")
    fun `should load user details successfully for existing user_id`() {
        // Given
        val userId = "testuser"
        val userRole = Role(id = 1L, name = "ROLE_USER")
        val adminRole = Role(id = 2L, name = "ROLE_ADMIN")
        val user = User(
            id = UUID.randomUUID(),
            userId = userId,
            username = "Test User",
            email = "test@example.com",
            password = "encodedPassword",
            roles = mutableSetOf(userRole, adminRole)
        )

        every { userRepository.findByUserId(userId) } returns Optional.of(user)

        // When
        val userDetails = userDetailsService.loadUserByUsername(userId)

        // Then
        assertEquals(user.userId, userDetails.username)
        assertEquals(user.password, userDetails.password)
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("ROLE_USER")))
        assertTrue(userDetails.authorities.contains(SimpleGrantedAuthority("ROLE_ADMIN")))
        assertEquals(2, userDetails.authorities.size)
        assertTrue(userDetails.isEnabled)
        assertTrue(userDetails.isAccountNonExpired)
        assertTrue(userDetails.isAccountNonLocked)
        assertTrue(userDetails.isCredentialsNonExpired)
    }

    @Test
    @DisplayName("존재하지 않는 user_id로 조회 시 UsernameNotFoundException을 발생시켜야 한다")
    fun `should throw UsernameNotFoundException for non-existing user_id`() {
        // Given
        val userId = "nonexistentuser"
        every { userRepository.findByUserId(userId) } returns Optional.empty()

        // When & Then
        val exception = assertThrows<UsernameNotFoundException> {
            userDetailsService.loadUserByUsername(userId)
        }
        assertEquals("User not found with user_id: $userId", exception.message)
    }

    @Test
    @DisplayName("비활성화된 사용자의 UserDetails 상태가 올바르게 설정되어야 한다")
    fun `should correctly set UserDetails state for a disabled user`() {
        // Given
        val userId = "disableduser"
        val user = User(
            id = UUID.randomUUID(),
            userId = userId,
            username = "Disabled User",
            email = "disabled@example.com",
            password = "password",
            enabled = false,
            accountNonLocked = false
        )
        every { userRepository.findByUserId(userId) } returns Optional.of(user)

        // When
        val userDetails = userDetailsService.loadUserByUsername(userId)

        // Then
        assertFalse(userDetails.isEnabled, "UserDetails should be disabled")
        assertFalse(userDetails.isAccountNonLocked, "UserDetails account should be locked")
    }
}