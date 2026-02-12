package sso.eum.kr.auth.service.dto

data class ClientDetailsInfoUpdateRequest(
    val appName: String?,
    val description: String?,
    val ownerEmail: String?,
    val appStatus: String?
)