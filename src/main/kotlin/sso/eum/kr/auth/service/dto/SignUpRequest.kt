package sso.eum.kr.auth.service.dto

data class SignUpRequest(
    // User Info
    val account: String,
    val username: String,
    val email: String,
    val password: String,
    val deptName: String?,
    val phoneNumber: String,

    // Org Info
    val orgType: String,
    val bizName: String,
    val bizNumber: String,
    val repName: String,
    val address: String
)