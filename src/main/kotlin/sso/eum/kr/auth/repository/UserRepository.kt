package sso.eum.kr.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import sso.eum.kr.auth.domain.User
import java.util.Optional
import java.util.UUID

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUserId(userId: String): Optional<User>
    fun existsByUserId(userId: String): Boolean
    fun existsByEmail(email: String): Boolean
}