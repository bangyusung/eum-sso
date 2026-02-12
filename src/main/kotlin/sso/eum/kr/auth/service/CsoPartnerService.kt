package sso.eum.kr.auth.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.CsoPartner
import sso.eum.kr.auth.repository.CsoPartnerRepository
import sso.eum.kr.auth.repository.OrgRepository
import sso.eum.kr.auth.service.dto.CsoPartnerCreateRequest
import sso.eum.kr.auth.service.dto.CsoPartnerResponse
import sso.eum.kr.auth.service.dto.CsoPartnerUpdateRequest
import java.util.UUID

@Service
@Transactional(readOnly = true)
class CsoPartnerService(
    private val partnerRepository: CsoPartnerRepository,
    private val orgRepository: OrgRepository
) {

    @Transactional
    fun createPartner(request: CsoPartnerCreateRequest): CsoPartnerResponse {
        if (partnerRepository.findByBizNumber(request.bizNumber).isPresent) {
            throw IllegalStateException("Partner with biz_number '${request.bizNumber}' already exists.")
        }
        val org = request.orgId?.let { orgRepository.findById(it).orElse(null) }
        val partner = CsoPartner(
            organization = org,
            bizName = request.bizName,
            bizNumber = request.bizNumber,
            repName = request.repName,
            address = request.address
        )
        return CsoPartnerResponse.from(partnerRepository.save(partner))
    }

    fun findAllPartners(): List<CsoPartnerResponse> {
        return partnerRepository.findAll().map { CsoPartnerResponse.from(it) }
    }

    fun findPartnerById(id: UUID): CsoPartnerResponse {
        return partnerRepository.findById(id)
            .map { CsoPartnerResponse.from(it) }
            .orElseThrow { EntityNotFoundException("Partner not found with id: $id") }
    }

    @Transactional
    fun updatePartner(id: UUID, request: CsoPartnerUpdateRequest): CsoPartnerResponse {
        val partner = partnerRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Partner not found with id: $id") }

        request.orgId?.let { partner.organization = orgRepository.findById(it).orElse(null) }
        request.bizName?.let { partner.bizName = it }
        request.repName?.let { partner.repName = it }
        request.address?.let { partner.address = it }

        return CsoPartnerResponse.from(partnerRepository.save(partner))
    }

    @Transactional
    fun deletePartner(id: UUID) {
        val partner = partnerRepository.findById(id)
            .orElseThrow { EntityNotFoundException("Partner not found with id: $id") }
        partner.deleted = true
        partnerRepository.save(partner)
    }
}