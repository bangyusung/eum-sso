package sso.eum.kr.auth.controller

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sso.eum.kr.auth.service.BoardService
import sso.eum.kr.auth.service.dto.BoardCreateRequest
import sso.eum.kr.auth.service.dto.BoardResponse
import sso.eum.kr.auth.service.dto.BoardUpdateRequest
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/boards")
class BoardController(private val boardService: BoardService) {

    @PostMapping
    fun createBoard(@RequestBody request: BoardCreateRequest): ResponseEntity<BoardResponse> {
        val board = boardService.createBoard(request)
        return ResponseEntity.created(URI.create("/api/v1/boards/${board.id}")).body(board)
    }

    @GetMapping
    fun getAllBoards(): ResponseEntity<List<BoardResponse>> {
        return ResponseEntity.ok(boardService.findAllBoards())
    }

    @GetMapping("/{id}")
    fun getBoardById(@PathVariable id: UUID): ResponseEntity<BoardResponse> {
        return ResponseEntity.ok(boardService.findBoardById(id))
    }

    @PutMapping("/{id}")
    fun updateBoard(@PathVariable id: UUID, @RequestBody request: BoardUpdateRequest): ResponseEntity<BoardResponse> {
        val updatedBoard = boardService.updateBoard(id, request)
        return ResponseEntity.ok(updatedBoard)
    }

    @DeleteMapping("/{id}")
    fun deleteBoard(@PathVariable id: UUID): ResponseEntity<Void> {
        boardService.deleteBoard(id)
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Entity not found")))
    }
}