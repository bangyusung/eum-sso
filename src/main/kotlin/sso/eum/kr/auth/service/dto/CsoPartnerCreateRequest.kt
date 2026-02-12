package sso.eum.kr.auth.service.dto

import java.util.UUID

data class CsoPartnerCreateRequest(
    val orgId: UUID?,
    val bizName: String,
    val bizNumber: String,
    val repName: String?,
    val address: String?
)