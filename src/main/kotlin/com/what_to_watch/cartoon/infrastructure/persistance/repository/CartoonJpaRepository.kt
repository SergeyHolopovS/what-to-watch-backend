package com.what_to_watch.cartoon.infrastructure.persistance.repository

import com.what_to_watch.cartoon.infrastructure.persistance.entity.CartoonJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface CartoonJpaRepository : JpaRepository<CartoonJpaEntity, UUID> {

    @Query(
        value = "SELECT * FROM cartoon_jpa_entity WHERE rating = 0 ORDER BY RANDOM() LIMIT 1",
        nativeQuery = true,
    )
    fun findRandomWithZeroRating(): CartoonJpaEntity?

    fun existsByTitle(title: String): Boolean

    @Query(
        "SELECT c FROM CartoonJpaEntity c " +
                "WHERE (:title IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%'))) " +
                "AND (:studioId IS NULL OR c.studioId = :studioId) " +
                "ORDER BY c.createdAt DESC"
    )
    fun search(@Param("title") title: String?, @Param("studioId") studioId: UUID?): List<CartoonJpaEntity>

}
