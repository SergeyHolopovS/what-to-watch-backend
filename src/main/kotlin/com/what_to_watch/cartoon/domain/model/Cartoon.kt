package com.what_to_watch.cartoon.domain.model

import java.util.UUID

data class Cartoon(
    val id: UUID,
    val title: String,
    val releaseYear: Int,
    val rating: Int,
    val genre: String,
    val duration: Int,
    val type: String,
)
