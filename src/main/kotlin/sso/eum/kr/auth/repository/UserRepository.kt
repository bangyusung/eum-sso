package sso.eum.kr.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import sso.eum.kr.auth.domain.User
import java.util.Optional

interface UserRepository : JpaRepository<User, Int> {
    fun findByAccount(account: String): Optional<User>
    fun existsByAccount(account: String): Boolean
    fun existsByEmail(email: String): Boolean
}