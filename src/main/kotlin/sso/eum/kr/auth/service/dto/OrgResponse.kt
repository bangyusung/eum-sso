package sso.eum.kr.auth.service.dto

import sso.eum.kr.auth.domain.Org
import java.time.ZonedDateTime
import java.util.UUID

data class OrgResponse(
    val id: UUID?,
    val orgType: String,
    val bizName: String,
    val bizNumber: String,
    val repName: String?,
    val address: String?,
    val bizDocUrl: String?,
    val status: String,
    val createdAt: ZonedDateTime?
) {
    companion object {
        fun from(org: Org): OrgResponse {
            return OrgResponse(
                id = org.id,
                orgType = org.orgType,
                bizName = org.bizName,
                bizNumber = org.bizNumber,
                repName = org.repName,
                address = org.address,
                bizDocUrl = org.bizDocUrl,
                status = org.status,
                createdAt = org.createdAt
            )
        }
    }
}