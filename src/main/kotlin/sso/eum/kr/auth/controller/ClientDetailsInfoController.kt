package sso.eum.kr.auth.controller

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sso.eum.kr.auth.service.ClientDetailsInfoService
import sso.eum.kr.auth.service.dto.ClientDetailsInfoCreateRequest
import sso.eum.kr.auth.service.dto.ClientDetailsInfoResponse
import sso.eum.kr.auth.service.dto.ClientDetailsInfoUpdateRequest
import java.net.URI

@RestController
@RequestMapping("/api/v1/client-details")
class ClientDetailsInfoController(private val service: ClientDetailsInfoService) {

    @GetMapping
    fun getAll(): ResponseEntity<List<ClientDetailsInfoResponse>> {
        return ResponseEntity.ok(service.findAll())
    }

    @GetMapping("/{clientId}")
    fun getById(@PathVariable clientId: String): ResponseEntity<ClientDetailsInfoResponse> {
        return ResponseEntity.ok(service.findById(clientId))
    }

    @PostMapping
    fun create(@RequestBody request: ClientDetailsInfoCreateRequest): ResponseEntity<ClientDetailsInfoResponse> {
        val details = service.create(request)
        return ResponseEntity.created(URI.create("/api/v1/client-details/${details.clientId}")).body(details)
    }

    @PutMapping("/{clientId}")
    fun update(@PathVariable clientId: String, @RequestBody request: ClientDetailsInfoUpdateRequest): ResponseEntity<ClientDetailsInfoResponse> {
        val updatedDetails = service.update(clientId, request)
        return ResponseEntity.ok(updatedDetails)
    }

    @DeleteMapping("/{clientId}")
    fun delete(@PathVariable clientId: String): ResponseEntity<Void> {
        service.delete(clientId)
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Entity not found")))
    }

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalState(ex: IllegalStateException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("error" to (ex.message ?: "Invalid request")))
    }
}