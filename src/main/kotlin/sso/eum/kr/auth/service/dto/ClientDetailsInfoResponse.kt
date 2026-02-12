package sso.eum.kr.auth.service.dto

import sso.eum.kr.auth.domain.ClientDetailsInfo
import java.time.Instant

data class ClientDetailsInfoResponse(
    val clientId: String,
    val appName: String,
    val description: String?,
    val ownerEmail: String?,
    val appStatus: String?,
    val createdAt: Instant?,
    val updatedAt: Instant?
) {
    companion object {
        fun from(details: ClientDetailsInfo): ClientDetailsInfoResponse {
            return ClientDetailsInfoResponse(
                clientId = details.clientId,
                appName = details.appName,
                description = details.description,
                ownerEmail = details.ownerEmail,
                appStatus = details.appStatus,
                createdAt = details.createdAt,
                updatedAt = details.updatedAt
            )
        }
    }
}