package sso.eum.kr.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import sso.eum.kr.auth.domain.CsoPartner
import java.util.Optional
import java.util.UUID

interface CsoPartnerRepository : JpaRepository<CsoPartner, UUID> {
    fun findByBizNumber(bizNumber: String): Optional<CsoPartner>
}