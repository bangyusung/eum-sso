package sso.eum.kr.auth.domain

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null

    @Column(nullable = false, unique = true, length = 50)
    lateinit var account: String

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    lateinit var org: Org

    @Column(nullable = false, length = 50)
    lateinit var username: String

    @Column(nullable = false, length = 255)
    lateinit var password: String

    @Column(nullable = false, length = 100)
    lateinit var email: String

    @Column(name = "dept_name", length = 100)
    var deptName: String? = null

    @Column(name = "phone_number", nullable = false, length = 20)
    lateinit var phoneNumber: String

    @Column(nullable = false)
    var locked: Boolean = false

    @Column(length = 20)
    var status: String = "PENDING"

    @Column(name = "user_role", nullable = false, length = 20)
    var userRole: String = "STAFF"

    @Column(name = "last_login_at")
    var lastLoginAt: Instant? = null

    @Column(nullable = false)
    var deleted: Boolean = false

    @Column(name = "modified_at", updatable = false, insertable = false)
    val modifiedAt: Instant? = null

    @Column(name = "created_at", updatable = false, insertable = false)
    val createdAt: Instant? = null

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()
}