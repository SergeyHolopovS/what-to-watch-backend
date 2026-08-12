package com.what_to_watch.series.domain.model

import java.time.Instant
import java.util.UUID

data class Series(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val rating: Int,
    val genre: String,
    val duration: Int,
    val type: String,
    val studioId: UUID,
    val createdAt: Instant,
)
