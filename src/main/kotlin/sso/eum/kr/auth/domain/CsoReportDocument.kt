package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "cso_report_documents")
class CsoReportDocument(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "re_entrustment_report_id", nullable = false)
    var report: CsoReEntrustmentReport,
    @Column(name = "doc_type", nullable = false, length = 30)
    var docType: String = "",
    @OneToOne(fetch = FetchType.LAZY) @JoinColumn(name = "s3file_id", nullable = false)
    var s3file: S3File,
    var deleted: Boolean = false,
    @UpdateTimestamp @Column(name = "modified_at")
    var modifiedAt: ZonedDateTime? = null,
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    val createdAt: ZonedDateTime? = null
)