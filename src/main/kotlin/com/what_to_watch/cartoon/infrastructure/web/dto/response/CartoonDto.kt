package com.what_to_watch.cartoon.infrastructure.web.dto.response

import java.util.UUID

data class CartoonDto(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val rating: Int,
)
