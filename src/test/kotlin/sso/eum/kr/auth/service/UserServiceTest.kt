package sso.eum.kr.auth.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import sso.eum.kr.auth.domain.Role
import sso.eum.kr.auth.domain.User
import sso.eum.kr.auth.repository.RoleRepository
import sso.eum.kr.auth.repository.UserRepository
import sso.eum.kr.auth.service.dto.UserRegistrationRequest
import java.util.*

class UserServiceTest {

    private lateinit var userRepository: UserRepository
    private lateinit var roleRepository: RoleRepository
    private lateinit var passwordEncoder: PasswordEncoder
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userRepository = mockk()
        roleRepository = mockk()
        passwordEncoder = mockk()
        userService = UserService(userRepository, roleRepository, passwordEncoder)
    }

    @Test
    @DisplayName("새로운 사용자를 성공적으로 등록해야 한다")
    fun `should register a new user successfully`() {
        // Given
        val request = UserRegistrationRequest("testuser", "Test User", "test@example.com", "password123")
        val encodedPassword = "encodedPassword123"
        val userRole = Role(id = 1L, name = "ROLE_USER")
        val savedUser = User(
            id = UUID.randomUUID(), // 여기에 UUID를 할당하여 null이 아니도록 수정
            userId = request.userId,
            username = request.username,
            email = request.email,
            password = encodedPassword,
            roles = mutableSetOf(userRole)
        )

        every { userRepository.existsByUserId(request.userId) } returns false
        every { userRepository.existsByEmail(request.email) } returns false
        every { roleRepository.findByName("ROLE_USER") } returns Optional.of(userRole)
        every { passwordEncoder.encode(request.password) } returns encodedPassword
        every { userRepository.save(any<User>()) } returns savedUser

        // When
        val result = userService.registerUser(request)

        // Then
        assertNotNull(result.id) // 이제 이 검증이 통과됩니다.
        assertEquals(request.userId, result.userId)
        assertEquals(request.username, result.username)
        assertEquals(request.email, result.email)
        assertEquals(encodedPassword, result.password)
        assertTrue(result.roles.contains(userRole))

        verify(exactly = 1) { userRepository.existsByUserId(request.userId) }
        verify(exactly = 1) { userRepository.existsByEmail(request.email) }
        verify(exactly = 1) { roleRepository.findByName("ROLE_USER") }
        verify(exactly = 1) { passwordEncoder.encode(request.password) }
        verify(exactly = 1) { userRepository.save(any<User>()) }
    }

    @Test
    @DisplayName("중복된 User ID로 사용자 등록 시 IllegalStateException을 발생시켜야 한다")
    fun `should throw IllegalStateException when registering with duplicate userId`() {
        // Given
        val request = UserRegistrationRequest("existinguser", "Existing User", "test@example.com", "password123")
        every { userRepository.existsByUserId(request.userId) } returns true

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            userService.registerUser(request)
        }
        assertEquals("User ID 'existinguser' is already taken.", exception.message)
        verify(exactly = 1) { userRepository.existsByUserId(request.userId) }
        verify(exactly = 0) { userRepository.existsByEmail(any()) }
        verify(exactly = 0) { roleRepository.findByName(any()) }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    @DisplayName("중복된 이메일로 사용자 등록 시 IllegalStateException을 발생시켜야 한다")
    fun `should throw IllegalStateException when registering with duplicate email`() {
        // Given
        val request = UserRegistrationRequest("newuser", "New User", "existing@example.com", "password123")
        every { userRepository.existsByUserId(request.userId) } returns false
        every { userRepository.existsByEmail(request.email) } returns true

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            userService.registerUser(request)
        }
        assertEquals("Email 'existing@example.com' is already in use.", exception.message)
        verify(exactly = 1) { userRepository.existsByUserId(request.userId) }
        verify(exactly = 1) { userRepository.existsByEmail(request.email) }
        verify(exactly = 0) { roleRepository.findByName(any()) }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }

    @Test
    @DisplayName("기본 역할이 없을 경우 IllegalStateException을 발생시켜야 한다")
    fun `should throw IllegalStateException if default role is not found`() {
        // Given
        val request = UserRegistrationRequest("testuser", "Test User", "test@example.com", "password123")
        every { userRepository.existsByUserId(request.userId) } returns false
        every { userRepository.existsByEmail(request.email) } returns false
        every { roleRepository.findByName("ROLE_USER") } returns Optional.empty()

        // When & Then
        val exception = assertThrows<IllegalStateException> {
            userService.registerUser(request)
        }
        assertEquals("Default role 'ROLE_USER' not found.", exception.message)
        verify(exactly = 1) { userRepository.existsByUserId(request.userId) }
        verify(exactly = 1) { userRepository.existsByEmail(request.email) }
        verify(exactly = 1) { roleRepository.findByName("ROLE_USER") }
        verify(exactly = 0) { passwordEncoder.encode(any()) }
        verify(exactly = 0) { userRepository.save(any()) }
    }
}