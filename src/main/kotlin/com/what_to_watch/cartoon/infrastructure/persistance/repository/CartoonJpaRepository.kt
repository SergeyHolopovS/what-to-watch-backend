package com.what_to_watch.cartoon.infrastructure.persistance.repository

import com.what_to_watch.cartoon.infrastructure.persistance.entity.CartoonJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.UUID

interface CartoonJpaRepository : JpaRepository<CartoonJpaEntity, UUID> {

    @Query(
        value = "SELECT * FROM cartoon_jpa_entity WHERE rating = 0 ORDER BY RANDOM() LIMIT 1",
        nativeQuery = true,
    )
    fun findRandomWithZeroRating(): CartoonJpaEntity?

    fun existsByTitle(title: String): Boolean

    fun findByTitleContainingIgnoreCase(title: String): List<CartoonJpaEntity>

}
