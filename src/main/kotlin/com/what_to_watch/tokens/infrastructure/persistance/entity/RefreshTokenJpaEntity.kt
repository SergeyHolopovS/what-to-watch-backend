package com.what_to_watch.tokens.infrastructure.persistance.entity

import com.what_to_watch.user.infrastructure.persistance.entity.UserJpaEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@Entity
data class RefreshTokenJpaEntity(

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @JoinColumn(name = "user_id")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    val user: UserJpaEntity,

    @Column(nullable = false, length = 1024)
    val token: String,

    @Column(nullable = false)
    val tokenPairId: String,

    @Column(nullable = false)
    val active: Boolean = true,

    @CreationTimestamp
    val createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    val expiresAt: Instant,

) : Serializable
