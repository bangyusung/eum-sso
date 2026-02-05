package sso.eum.kr.auth.service.dto

data class UserRegistrationRequest(
    val userId: String,
    val username: String,
    val email: String,
    val password: String
)