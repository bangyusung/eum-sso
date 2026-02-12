package sso.eum.kr.auth.service.dto

import sso.eum.kr.auth.domain.Role

data class RoleResponse(
    val id: Long?,
    val name: String
) {
    companion object {
        fun from(role: Role): RoleResponse {
            return RoleResponse(
                id = role.id,
                name = role.name
            )
        }
    }
}