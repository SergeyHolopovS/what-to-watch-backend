package com.what_to_watch.series.infrastructure.web.dto.response

import java.util.UUID

data class SeriesDto(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val rating: Int,
    val genre: String,
    val duration: Int,
    val type: String,
    val studioId: UUID,
)
