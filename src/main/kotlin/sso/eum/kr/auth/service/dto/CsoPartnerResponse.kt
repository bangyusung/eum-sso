package sso.eum.kr.auth.service.dto

import sso.eum.kr.auth.domain.CsoPartner
import java.time.ZonedDateTime
import java.util.UUID

data class CsoPartnerResponse(
    val id: UUID?,
    val orgId: UUID?,
    val bizName: String,
    val bizNumber: String,
    val repName: String?,
    val address: String?,
    val createdAt: ZonedDateTime?
) {
    companion object {
        fun from(partner: CsoPartner): CsoPartnerResponse {
            return CsoPartnerResponse(
                id = partner.id,
                orgId = partner.organization?.id,
                bizName = partner.bizName,
                bizNumber = partner.bizNumber,
                repName = partner.repName,
                address = partner.address,
                createdAt = partner.createdAt
            )
        }
    }
}