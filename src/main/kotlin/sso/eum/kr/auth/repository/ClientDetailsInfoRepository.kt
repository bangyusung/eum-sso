package sso.eum.kr.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import sso.eum.kr.auth.domain.ClientDetailsInfo

interface ClientDetailsInfoRepository : JpaRepository<ClientDetailsInfo, String>