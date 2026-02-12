package sso.eum.kr.auth.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.Org
import sso.eum.kr.auth.repository.OrgRepository
import sso.eum.kr.auth.service.dto.OrgCreateRequest
import sso.eum.kr.auth.service.dto.OrgResponse
import sso.eum.kr.auth.service.dto.OrgUpdateRequest
import java.util.UUID

@Service
@Transactional(readOnly = true)
class OrgService(private val orgRepository: OrgRepository) {

    @Transactional
    fun createOrg(request: OrgCreateRequest): OrgResponse {
        if (orgRepository.findByBizNumber(request.bizNumber).isPresent) {
            throw IllegalStateException("Organization with biz_number '${request.bizNumber}' already exists.")
        }
        val org = Org(
            orgType = request.orgType,
            bizName = request.bizName,
            bizNumber = request.bizNumber,
            repName = request.repName,
            address = request.address,
            bizDocUrl = request.bizDocUrl,
            status = request.status ?: "NORMAL"
        )
        return OrgResponse.from(orgRepository.save(org))
    }

    fun findAllOrgs(): List<OrgResponse> {
        return orgRepository.findAll().map { OrgResponse.from(it) }
    }

    fun findOrgById(id: UUID): OrgResponse {
        return orgRepository.findById(id)
            .map { OrgResponse.from(it) }
            .orElseThrow { EntityNotFoundException("Organization not found with id: $id") }
    }

    @Transactional
    fun updateOrg(id: UUID, request: OrgUpdateRequest): OrgResponse {
        val org = orgRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Organization not found with id: $id") }

        request.orgType?.let { org.orgType = it }
        request.bizName?.let { org.bizName = it }
        request.repName?.let { org.repName = it }
        request.address?.let { org.address = it }
        request.bizDocUrl?.let { org.bizDocUrl = it }
        request.status?.let { org.status = it }

        return OrgResponse.from(orgRepository.save(org))
    }

    @Transactional
    fun deleteOrg(id: UUID) {
        val org = orgRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Organization not found with id: $id") }
        org.deleted = true
        orgRepository.save(org)
    }
}