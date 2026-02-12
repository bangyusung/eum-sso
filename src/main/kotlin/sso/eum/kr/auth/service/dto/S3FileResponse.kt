package sso.eum.kr.auth.service.dto

import sso.eum.kr.auth.domain.S3File
import java.time.ZonedDateTime
import java.util.UUID

data class S3FileResponse(
    val id: UUID?,
    val url: String,
    val fileSize: Long?,
    val orphaned: Boolean?,
    val createdAt: ZonedDateTime?
) {
    companion object {
        fun from(file: S3File): S3FileResponse {
            return S3FileResponse(
                id = file.id,
                url = file.url,
                fileSize = file.fileSize,
                orphaned = file.orphaned,
                createdAt = file.createdAt
            )
        }
    }
}