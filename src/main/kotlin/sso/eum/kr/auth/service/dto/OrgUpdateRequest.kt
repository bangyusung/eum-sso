package sso.eum.kr.auth.service.dto

data class OrgUpdateRequest(
    val orgType: String?,
    val bizName: String?,
    val repName: String?,
    val address: String?,
    val bizDocUrl: String?,
    val status: String?
)