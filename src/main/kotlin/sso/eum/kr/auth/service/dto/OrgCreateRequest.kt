package sso.eum.kr.auth.service.dto

data class OrgCreateRequest(
    val orgType: String,
    val bizName: String,
    val bizNumber: String,
    val repName: String?,
    val address: String?,
    val bizDocUrl: String?,
    val status: String? = "NORMAL"
)