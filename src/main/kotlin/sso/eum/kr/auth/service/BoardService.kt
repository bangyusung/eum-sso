package sso.eum.kr.auth.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.Board
import sso.eum.kr.auth.repository.BoardRepository
import sso.eum.kr.auth.service.dto.BoardCreateRequest
import sso.eum.kr.auth.service.dto.BoardResponse
import sso.eum.kr.auth.service.dto.BoardUpdateRequest
import java.util.UUID

@Service
@Transactional(readOnly = true)
class BoardService(private val boardRepository: BoardRepository) {

    @Transactional
    fun createBoard(request: BoardCreateRequest): BoardResponse {
        val board = Board(
            category = request.category,
            title = request.title,
            content = request.content,
            isFixed = request.isFixed
        )
        return BoardResponse.from(boardRepository.save(board))
    }

    fun findAllBoards(): List<BoardResponse> {
        return boardRepository.findAll().map { BoardResponse.from(it) }
    }

    fun findBoardById(id: UUID): BoardResponse {
        return boardRepository.findById(id)
            .map { BoardResponse.from(it) }
            .orElseThrow { EntityNotFoundException("Board not found with id: $id") }
    }

    @Transactional
    fun updateBoard(id: UUID, request: BoardUpdateRequest): BoardResponse {
        val board = boardRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Board not found with id: $id") }

        request.category?.let { board.category = it }
        request.title?.let { board.title = it }
        request.content?.let { board.content = it }
        request.isFixed?.let { board.isFixed = it }

        return BoardResponse.from(boardRepository.save(board))
    }

    @Transactional
    fun deleteBoard(id: UUID) {
        val board = boardRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Board not found with id: $id") }
        board.deleted = true
        boardRepository.save(board)
    }
}