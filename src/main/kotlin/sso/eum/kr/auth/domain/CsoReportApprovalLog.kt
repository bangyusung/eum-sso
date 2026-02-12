package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "cso_report_approval_logs")
class CsoReportApprovalLog(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "report_id", nullable = false)
    var report: CsoReEntrustmentReport,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "act_user_id", nullable = false)
    var actionUser: User,
    @Column(name = "action_type", nullable = false, length = 20)
    var actionType: String = "",
    @Lob
    var comment: String? = null,
    var deleted: Boolean = false,
    @UpdateTimestamp @Column(name = "modified_at")
    var modifiedAt: ZonedDateTime? = null,
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    val createdAt: ZonedDateTime? = null
)