package com.what_to_watch.series.infrastructure.persistance.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.hibernate.annotations.CreationTimestamp
import java.io.Serializable
import java.time.Instant
import java.util.UUID

@Entity
data class SeriesJpaEntity(

    @Id
    @Column(nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID? = null,

    @Column(nullable = false, unique = true)
    val title: String,

    @Column(nullable = false)
    val rating: Int = 0,

    @Column(nullable = false, unique = false)
    val releaseYear: Int,

    @Column(nullable = false)
    val genre: String,

    @Column(nullable = false)
    val duration: Int,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val studioId: UUID,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val createdAt: Instant? = null,

) : Serializable
