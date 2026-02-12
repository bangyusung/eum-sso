package sso.eum.kr.auth.service.dto

import java.util.UUID

data class CsoPartnerUpdateRequest(
    val orgId: UUID?,
    val bizName: String?,
    val repName: String?,
    val address: String?
)