package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "cso_re_entrustment_reports")
class CsoReEntrustmentReport(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "pharma_org_id", nullable = false)
    var pharmaOrg: Org,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "cso_org_id", nullable = false)
    var csoOrg: Org,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "target_partner_id", nullable = false)
    var targetPartner: CsoPartner,
    @Column(name = "approval_status", length = 20)
    var approvalStatus: String? = "READY",
    @Column(name = "delivery_status", length = 20)
    var deliveryStatus: String? = "PROGRESS",
    @Column(name = "contract_start_date")
    var contractStartDate: LocalDate? = null,
    @Column(name = "contract_end_date")
    var contractEndDate: LocalDate? = null,
    @Column(name = "notice_date")
    var noticeDate: LocalDate? = null,
    @Column(name = "report_date")
    var reportDate: ZonedDateTime? = ZonedDateTime.now(),
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "report_user_id", nullable = false)
    var reportUser: User,
    var deleted: Boolean = false,
    @UpdateTimestamp @Column(name = "modified_at")
    var modifiedAt: ZonedDateTime? = null,
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    val createdAt: ZonedDateTime? = null
)