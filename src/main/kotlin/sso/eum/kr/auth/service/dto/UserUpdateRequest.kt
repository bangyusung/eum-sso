package sso.eum.kr.auth.service.dto

import java.util.UUID

data class UserUpdateRequest(
    val username: String?,
    val email: String?,
    val enabled: Boolean?,
    val orgId: UUID?,
    val deptName: String?,
    val phoneNumber: String?,
    val userRole: String?
)