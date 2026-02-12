package sso.eum.kr.auth.service.dto

import java.util.UUID

data class UserRegistrationRequest(
    val userId: String,
    val username: String,
    val email: String,
    val password: String,
    val orgId: UUID? = null,
    val deptName: String? = null,
    val phoneNumber: String? = null,
    val userRole: String? = "STAFF",
    val roles: Set<String> = setOf("ROLE_USER") // 역할 목록 추가
)