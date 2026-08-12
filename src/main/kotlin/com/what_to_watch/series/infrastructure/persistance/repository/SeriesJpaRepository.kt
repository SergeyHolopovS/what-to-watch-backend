package com.what_to_watch.series.infrastructure.persistance.repository

import com.what_to_watch.series.infrastructure.persistance.entity.SeriesJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface SeriesJpaRepository : JpaRepository<SeriesJpaEntity, UUID> {

    @Query(
        value = "SELECT * FROM series_jpa_entity WHERE rating = 0 ORDER BY RANDOM() LIMIT 1",
        nativeQuery = true,
    )
    fun findRandomWithZeroRating(): SeriesJpaEntity?

    fun existsByTitle(title: String): Boolean

    @Query(
        "SELECT s FROM SeriesJpaEntity s " +
            "WHERE (:title IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%'))) " +
            "AND (:studioId IS NULL OR s.studioId = :studioId) " +
            "ORDER BY s.createdAt DESC"
    )
    fun search(@Param("title") title: String?, @Param("studioId") studioId: UUID?): List<SeriesJpaEntity>

}
