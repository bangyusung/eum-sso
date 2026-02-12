package sso.eum.kr.auth.service.dto

data class ClientDetailsInfoCreateRequest(
    val clientId: String,
    val appName: String,
    val description: String?,
    val ownerEmail: String?,
    val appStatus: String?
)