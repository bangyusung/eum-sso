package sso.eum.kr.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import sso.eum.kr.auth.domain.Org
import java.util.Optional
import java.util.UUID

interface OrgRepository : JpaRepository<Org, UUID> {
    fun findByBizNumber(bizNumber: String): Optional<Org>
}