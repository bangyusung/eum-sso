package sso.eum.kr.auth.service.dto

import sso.eum.kr.auth.domain.Board
import java.time.ZonedDateTime
import java.util.UUID

data class BoardResponse(
    val id: UUID?,
    val category: String,
    val title: String,
    val content: String,
    val isFixed: Boolean?,
    val createdAt: ZonedDateTime?
) {
    companion object {
        fun from(board: Board): BoardResponse {
            return BoardResponse(
                id = board.id,
                category = board.category,
                title = board.title,
                content = board.content,
                isFixed = board.isFixed,
                createdAt = board.createdAt
            )
        }
    }
}