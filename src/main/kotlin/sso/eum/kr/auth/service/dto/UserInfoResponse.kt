package sso.eum.kr.auth.service.dto

data class UserInfoResponse(
    val id: Int,
    val account: String,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val deptName: String?,
    val status: String,
    val roles: Set<String>,
    val org: OrgInfo
) {
    data class OrgInfo(
        val id: Int,
        val bizName: String,
        val orgType: String
    )
}