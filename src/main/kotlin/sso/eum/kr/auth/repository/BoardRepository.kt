package sso.eum.kr.auth.repository

import org.springframework.data.jpa.repository.JpaRepository
import sso.eum.kr.auth.domain.Board
import java.util.UUID

interface BoardRepository : JpaRepository<Board, UUID>