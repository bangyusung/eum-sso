package sso.eum.kr.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import sso.eum.kr.auth.domain.S3File
import java.util.UUID

interface S3FileRepository : JpaRepository<S3File, UUID>