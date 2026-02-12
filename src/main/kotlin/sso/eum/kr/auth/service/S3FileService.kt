package sso.eum.kr.auth.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.S3File
import sso.eum.kr.auth.repository.S3FileRepository
import sso.eum.kr.auth.service.dto.S3FileCreateRequest
import sso.eum.kr.auth.service.dto.S3FileResponse
import sso.eum.kr.auth.service.dto.S3FileUpdateRequest
import java.util.UUID

@Service
@Transactional(readOnly = true)
class S3FileService(private val s3FileRepository: S3FileRepository) {

    @Transactional
    fun createS3File(request: S3FileCreateRequest): S3FileResponse {
        val s3File = S3File(
            url = request.url,
            fileSize = request.fileSize
        )
        return S3FileResponse.from(s3FileRepository.save(s3File))
    }

    fun findAllS3Files(): List<S3FileResponse> {
        return s3FileRepository.findAll().map { S3FileResponse.from(it) }
    }

    fun findS3FileById(id: UUID): S3FileResponse {
        return s3FileRepository.findById(id)
            .map { S3FileResponse.from(it) }
            .orElseThrow { EntityNotFoundException("S3File not found with id: $id") }
    }

    @Transactional
    fun updateS3File(id: UUID, request: S3FileUpdateRequest): S3FileResponse {
        val s3File = s3FileRepository.findById(id)
            .orElseThrow { EntityNotFoundException("S3File not found with id: $id") }

        request.orphaned?.let { s3File.orphaned = it }

        return S3FileResponse.from(s3FileRepository.save(s3File))
    }

    @Transactional
    fun deleteS3File(id: UUID) {
        val s3File = s3FileRepository.findById(id)
            .orElseThrow { EntityNotFoundException("S3File not found with id: $id") }
        s3File.deleted = true
        s3FileRepository.save(s3File)
    }
}