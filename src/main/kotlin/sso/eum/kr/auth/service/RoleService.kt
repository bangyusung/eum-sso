package sso.eum.kr.auth.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.Role
import sso.eum.kr.auth.repository.RoleRepository
import sso.eum.kr.auth.service.dto.RoleCreateRequest
import sso.eum.kr.auth.service.dto.RoleResponse

@Service
@Transactional(readOnly = true)
class RoleService(private val roleRepository: RoleRepository) {

    fun findAllRoles(): List<RoleResponse> {
        return roleRepository.findAll().map { RoleResponse.from(it) }
    }

    fun findRoleById(id: Long): RoleResponse {
        val role = roleRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Role not found with id: $id") }
        return RoleResponse.from(role)
    }

    @Transactional
    fun createRole(request: RoleCreateRequest): RoleResponse {
        if (roleRepository.findByName(request.name).isPresent) {
            throw IllegalStateException("Role name '${request.name}' already exists.")
        }
        val role = Role(name = request.name)
        val savedRole = roleRepository.save(role)
        return RoleResponse.from(savedRole)
    }

    @Transactional
    fun deleteRole(id: Long) {
        if (!roleRepository.existsById(id)) {
            throw EntityNotFoundException("Role not found with id: $id")
        }
        // Note: Deleting a role that is currently assigned to users will cause a constraint violation.
        // Proper handling would involve checking for assignments before deletion.
        roleRepository.deleteById(id)
    }
}