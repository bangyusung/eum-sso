package sso.eum.kr.auth.controller

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sso.eum.kr.auth.service.S3FileService
import sso.eum.kr.auth.service.dto.S3FileCreateRequest
import sso.eum.kr.auth.service.dto.S3FileResponse
import sso.eum.kr.auth.service.dto.S3FileUpdateRequest
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/s3files")
class S3FileController(private val s3FileService: S3FileService) {

    @PostMapping
    fun createS3File(@RequestBody request: S3FileCreateRequest): ResponseEntity<S3FileResponse> {
        val file = s3FileService.createS3File(request)
        return ResponseEntity.created(URI.create("/api/v1/s3files/${file.id}")).body(file)
    }

    @GetMapping
    fun getAllS3Files(): ResponseEntity<List<S3FileResponse>> {
        return ResponseEntity.ok(s3FileService.findAllS3Files())
    }

    @GetMapping("/{id}")
    fun getS3FileById(@PathVariable id: UUID): ResponseEntity<S3FileResponse> {
        return ResponseEntity.ok(s3FileService.findS3FileById(id))
    }

    @PutMapping("/{id}")
    fun updateS3File(@PathVariable id: UUID, @RequestBody request: S3FileUpdateRequest): ResponseEntity<S3FileResponse> {
        val updatedFile = s3FileService.updateS3File(id, request)
        return ResponseEntity.ok(updatedFile)
    }

    @DeleteMapping("/{id}")
    fun deleteS3File(@PathVariable id: UUID): ResponseEntity<Void> {
        s3FileService.deleteS3File(id)
        return ResponseEntity.noContent().build()
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(mapOf("error" to (ex.message ?: "Entity not found")))
    }
}