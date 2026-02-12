package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "orgs")
class Org(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(name = "org_type", nullable = false, length = 20)
    var orgType: String = "",

    @Column(name = "biz_name", nullable = false, length = 100)
    var bizName: String = "",

    @Column(name = "biz_number", nullable = false, unique = true, length = 20)
    var bizNumber: String = "",

    @Column(name = "rep_name", length = 50)
    var repName: String? = null,

    @Column
    var address: String? = null,

    @Column(name = "biz_doc_url", length = 500)
    var bizDocUrl: String? = null,

    @Column(nullable = false, length = 20)
    var status: String = "NORMAL",

    @Column(nullable = false)
    var deleted: Boolean = false,

    @UpdateTimestamp
    @Column(name = "modified_at")
    var modifiedAt: ZonedDateTime? = null,

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    val createdAt: ZonedDateTime? = null
)