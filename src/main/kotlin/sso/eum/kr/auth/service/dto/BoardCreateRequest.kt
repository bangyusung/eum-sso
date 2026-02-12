package sso.eum.kr.auth.service.dto

data class BoardCreateRequest(
    val category: String,
    val title: String,
    val content: String,
    val isFixed: Boolean? = false
)