package sso.eum.kr.auth.domain

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "boards")
class Board(
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    @Column(nullable = false, length = 20)
    var category: String = "",
    @Column(nullable = false, length = 200)
    var title: String = "",
    @Lob @Column(nullable = false)
    var content: String = "",
    @Column(name = "is_fixed")
    var isFixed: Boolean? = false,
    var deleted: Boolean = false,
    @UpdateTimestamp @Column(name = "modified_at")
    var modifiedAt: ZonedDateTime? = null,
    @CreationTimestamp @Column(name = "created_at", updatable = false)
    val createdAt: ZonedDateTime? = null
)