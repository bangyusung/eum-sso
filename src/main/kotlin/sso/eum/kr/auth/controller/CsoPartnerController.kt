package sso.eum.kr.auth.controller

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sso.eum.kr.auth.service.CsoPartnerService
import sso.eum.kr.auth.service.dto.CsoPartnerCreateRequest
import sso.eum.kr.auth.service.dto.CsoPartnerResponse
import sso.eum.kr.auth.service.dto.CsoPartnerUpdateRequest
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/cso-partners")
class CsoPartnerController(private val partnerService: CsoPartnerService) {

    @PostMapping
    fun createPartner(@RequestBody request: CsoPartnerCreateRequest): ResponseEntity<CsoPartnerResponse> {
        val partner = partnerService.createPartner(request)
        return ResponseEntity.created(URI.create("/api/v1/cso-partners/${partner.id}")).body(partner)
    }

    @GetMapping
    fun getAllPartners(): ResponseEntity<List<CsoPartnerResponse>> {
        return ResponseEntity.ok(partnerService.findAllPartners())
    }

    @GetMapping("/{id}")
    fun getPartnerById(@PathVariable id: UUID): ResponseEntity<CsoPartnerResponse> {
        return ResponseEntity.ok(partnerService.findPartnerById(id))
    }

    @PutMapping("/{id}")
    fun updatePartner(@PathVariable id: UUID, @RequestBody request: CsoPartnerUpdateRequest): ResponseEntity<CsoPartnerResponse> {
        val updatedPartner = partnerService.updatePartner(id, request)
        return ResponseEntity.ok(updatedPartner)
    }

    @DeleteMapping("/{id}")
    fun deletePartner(@PathVariable id: UUID): ResponseEntity<Void> {
        partnerService.deletePartner(id)
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