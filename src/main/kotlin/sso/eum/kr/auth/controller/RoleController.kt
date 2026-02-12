package sso.eum.kr.auth.controller

import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sso.eum.kr.auth.service.RoleService
import sso.eum.kr.auth.service.dto.RoleCreateRequest
import sso.eum.kr.auth.service.dto.RoleResponse
import java.net.URI

@RestController
@RequestMapping("/api/v1/roles")
class RoleController(private val roleService: RoleService) {

    @GetMapping
    fun getAllRoles(): ResponseEntity<List<RoleResponse>> {
        return ResponseEntity.ok(roleService.findAllRoles())
    }

    @GetMapping("/{id}")
    fun getRoleById(@PathVariable id: Long): ResponseEntity<RoleResponse> {
        return ResponseEntity.ok(roleService.findRoleById(id))
    }

    @PostMapping
    fun createRole(@RequestBody request: RoleCreateRequest): ResponseEntity<RoleResponse> {
        val role = roleService.createRole(request)
        return ResponseEntity.created(URI.create("/api/v1/roles/${role.id}")).body(role)
    }

    @DeleteMapping("/{id}")
    fun deleteRole(@PathVariable id: Long): ResponseEntity<Void> {
        roleService.deleteRole(id)
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