package sso.eum.kr.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import sso.eum.kr.auth.domain.Org

interface OrgRepository : JpaRepository<Org, Int> {
    fun existsByBizNumber(bizNumber: String): Boolean
}