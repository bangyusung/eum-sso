package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,

    @Column(name = "user_id", nullable = false, unique = true, length = 100)
    var userId: String = "",

    @Column(nullable = false, length = 50)
    var username: String = "",

    @Column(nullable = false, length = 255)
    var password: String = "",

    @Column(nullable = false, length = 100)
    var email: String = "",

    @Column(nullable = false)
    var enabled: Boolean = true,

    @Column(name = "account_non_locked", nullable = false)
    var accountNonLocked: Boolean = true,

    @Column(name = "account_non_expired", nullable = false)
    var accountNonExpired: Boolean = true,

    @Column(name = "credentials_non_expired", nullable = false)
    var credentialsNonExpired: Boolean = true,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null,

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "users_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()
)