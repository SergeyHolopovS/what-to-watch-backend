package com.what_to_watch.user.infrastructure.persistance.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.io.Serializable
import java.util.UUID

@Entity
data class UserJpaEntity(

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val name: String,

    @Column(nullable = true, unique = false)
    val passwordHash: String? = null,

    @Column(nullable = false, unique = true)
    val discordId: Long,

    @Column(nullable = false, length = 512)
    val avatarUrl: String,

) : Serializable
