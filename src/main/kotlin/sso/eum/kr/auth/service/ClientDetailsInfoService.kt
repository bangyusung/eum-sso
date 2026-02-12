package sso.eum.kr.auth.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sso.eum.kr.auth.domain.ClientDetailsInfo
import sso.eum.kr.auth.repository.ClientDetailsInfoRepository
import sso.eum.kr.auth.service.dto.ClientDetailsInfoCreateRequest
import sso.eum.kr.auth.service.dto.ClientDetailsInfoResponse
import sso.eum.kr.auth.service.dto.ClientDetailsInfoUpdateRequest

@Service
@Transactional(readOnly = true)
class ClientDetailsInfoService(private val repository: ClientDetailsInfoRepository) {

    fun findAll(): List<ClientDetailsInfoResponse> {
        return repository.findAll().map { ClientDetailsInfoResponse.from(it) }
    }

    fun findById(clientId: String): ClientDetailsInfoResponse {
        val details = repository.findById(clientId)
            .orElseThrow { EntityNotFoundException("ClientDetailsInfo not found with id: $clientId") }
        return ClientDetailsInfoResponse.from(details)
    }

    @Transactional
    fun create(request: ClientDetailsInfoCreateRequest): ClientDetailsInfoResponse {
        if (repository.existsById(request.clientId)) {
            throw IllegalStateException("ClientDetailsInfo with id '${request.clientId}' already exists.")
        }
        val details = ClientDetailsInfo(
            clientId = request.clientId,
            appName = request.appName,
            description = request.description,
            ownerEmail = request.ownerEmail,
            appStatus = request.appStatus
        )
        val savedDetails = repository.save(details)
        return ClientDetailsInfoResponse.from(savedDetails)
    }

    @Transactional
    fun update(clientId: String, request: ClientDetailsInfoUpdateRequest): ClientDetailsInfoResponse {
        val details = repository.findById(clientId)
            .orElseThrow { EntityNotFoundException("ClientDetailsInfo not found with id: $clientId") }

        request.appName?.let { details.appName = it }
        request.description?.let { details.description = it }
        request.ownerEmail?.let { details.ownerEmail = it }
        request.appStatus?.let { details.appStatus = it }

        val updatedDetails = repository.save(details)
        return ClientDetailsInfoResponse.from(updatedDetails)
    }

    @Transactional
    fun delete(clientId: String) {
        if (!repository.existsById(clientId)) {
            throw EntityNotFoundException("ClientDetailsInfo not found with id: $clientId")
        }
        repository.deleteById(clientId)
    }
}