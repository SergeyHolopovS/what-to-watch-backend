package com.what_to_watch.catalog.infrastructure.web.dto.response

import java.time.Instant
import java.util.UUID

enum class CatalogContentType {
    CARTOON,
    SERIES,
}

data class CatalogItemDto(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val rating: Int,
    val genre: String,
    val duration: Int,
    val type: String,
    val studioId: UUID,
    val createdAt: Instant,
    val contentType: CatalogContentType,
)
