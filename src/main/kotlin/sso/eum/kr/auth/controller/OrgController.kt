package sso.eum.kr.auth.controller

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sso.eum.kr.auth.service.OrgService
import sso.eum.kr.auth.service.dto.OrgCreateRequest
import sso.eum.kr.auth.service.dto.OrgResponse
import sso.eum.kr.auth.service.dto.OrgUpdateRequest
import java.net.URI
import java.util.UUID

@RestController
@RequestMapping("/api/v1/orgs")
class OrgController(private val orgService: OrgService) {

    @PostMapping
    fun createOrg(@RequestBody request: OrgCreateRequest): ResponseEntity<OrgResponse> {
        val org = orgService.createOrg(request)
        return ResponseEntity.created(URI.create("/api/v1/orgs/${org.id}")).body(org)
    }

    @GetMapping
    fun getAllOrgs(): ResponseEntity<List<OrgResponse>> {
        return ResponseEntity.ok(orgService.findAllOrgs())
    }

    @GetMapping("/{id}")
    fun getOrgById(@PathVariable id: UUID): ResponseEntity<OrgResponse> {
        return ResponseEntity.ok(orgService.findOrgById(id))
    }

    @PutMapping("/{id}")
    fun updateOrg(@PathVariable id: UUID, @RequestBody request: OrgUpdateRequest): ResponseEntity<OrgResponse> {
        val updatedOrg = orgService.updateOrg(id, request)
        return ResponseEntity.ok(updatedOrg)
    }

    @DeleteMapping("/{id}")
    fun deleteOrg(@PathVariable id: UUID): ResponseEntity<Void> {
        orgService.deleteOrg(id)
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