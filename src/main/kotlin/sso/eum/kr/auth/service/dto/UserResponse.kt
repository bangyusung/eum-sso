package sso.eum.kr.auth.service.dto

import sso.eum.kr.auth.domain.User
import java.time.ZonedDateTime
import java.util.UUID

data class UserResponse(
    val id: UUID?,
    val userId: String,
    val username: String,
    val email: String,
    val enabled: Boolean,
    val orgId: UUID?,
    val deptName: String?,
    val phoneNumber: String?,
    val userRole: String,
    val lastLoginAt: ZonedDateTime?,
    val createdAt: ZonedDateTime?,
    val updatedAt: ZonedDateTime?,
    val roles: Set<String>
) {
    companion object {
        fun from(user: User): UserResponse {
            return UserResponse(
                id = user.id,
                userId = user.userId,
                username = user.username,
                email = user.email,
                enabled = user.enabled,
                orgId = user.organization?.id,
                deptName = user.deptName,
                phoneNumber = user.phoneNumber,
                userRole = user.userRole,
                lastLoginAt = user.lastLoginAt,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                roles = user.roles.map { it.name }.toSet()
            )
        }
    }
}