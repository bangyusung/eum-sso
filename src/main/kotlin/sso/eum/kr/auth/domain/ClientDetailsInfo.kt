package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "client_details_info")
class ClientDetailsInfo(
    @Id
    @Column(name = "client_id", length = 100)
    var clientId: String = "",

    @Column(name = "app_name", nullable = false, length = 100)
    var appName: String = "",

    @Column
    var description: String? = null,

    @Column(name = "owner_email", length = 100)
    var ownerEmail: String? = null,

    @Column(name = "app_status", length = 20)
    var appStatus: String? = "ACTIVE",

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null
)