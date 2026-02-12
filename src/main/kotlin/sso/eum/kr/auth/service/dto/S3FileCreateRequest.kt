package sso.eum.kr.auth.service.dto

data class S3FileCreateRequest(
    val url: String,
    val fileSize: Long?
)