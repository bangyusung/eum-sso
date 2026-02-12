package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "s3files")
class S3File(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(nullable = false, length = 500)
    var url: String = "",
    var fileSize: Long? = null,
    var orphaned: Boolean? = false,
    var deleted: Boolean = false,
    @UpdateTimestamp @Column(name = "modified_at")
    var modifiedAt: ZonedDateTime? = null,
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    val createdAt: ZonedDateTime? = null
)