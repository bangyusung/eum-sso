package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "cso_partners")
class CsoPartner(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "org_id")
    var organization: Org? = null,
    @Column(name = "biz_name", nullable = false, length = 100)
    var bizName: String = "",
    @Column(name = "biz_number", nullable = false, unique = true, length = 20)
    var bizNumber: String = "",
    @Column(name = "rep_name", length = 50)
    var repName: String? = null,
    var address: String? = null,
    var deleted: Boolean = false,
    @UpdateTimestamp @Column(name = "modified_at")
    var modifiedAt: ZonedDateTime? = null,
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    val createdAt: ZonedDateTime? = null
)