package com.what_to_watch.cartoon.application.usecase.create

data class CreateCartoonCommand(
    val title: String,
    val releaseYear: Int,
)
