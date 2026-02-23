package sso.eum.kr.auth.domain

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDate

@Entity
@Table(name = "orgs")
class Org {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @Column(name = "org_type", nullable = false, length = 10)
    lateinit var orgType: String

    @Column(name = "biz_name", nullable = false, length = 210)
    lateinit var bizName: String

    @Column(name = "biz_number", nullable = false, unique = true, length = 20)
    lateinit var bizNumber: String

    @Column(name = "rep_name", nullable = false, length = 50)
    lateinit var repName: String

    @Column(name = "address", nullable = false)
    lateinit var address: String

    @Column(name = "biz_doc_url")
    var bizDocUrl: String? = null

    @Column(name = "status", length = 20)
    var status: String = "NORMAL"

    @Column(name = "edu_completion_date")
    var eduCompletionDate: LocalDate? = null

    @Column(name = "deleted")
    var deleted: Boolean = false

    @Column(name = "modified_at", updatable = false, insertable = false)
    val modifiedAt: Instant? = null

    @Column(name = "created_at", updatable = false, insertable = false)
    val createdAt: Instant? = null
}