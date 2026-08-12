package com.what_to_watch.cartoon.application.usecase.create

import java.util.UUID

data class CreateCartoonCommand(
    val title: String,
    val releaseYear: Int,
    val genre: String,
    val duration: Int,
    val type: String,
    val studioId: UUID?,
    val studioName: String?,
)
