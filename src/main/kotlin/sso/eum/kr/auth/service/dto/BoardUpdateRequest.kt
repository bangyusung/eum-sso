package sso.eum.kr.auth.service.dto

data class BoardUpdateRequest(
    val category: String?,
    val title: String?,
    val content: String?,
    val isFixed: Boolean?
)