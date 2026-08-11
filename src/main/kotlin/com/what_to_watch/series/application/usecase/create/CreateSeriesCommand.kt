package com.what_to_watch.series.application.usecase.create

data class CreateSeriesCommand(
    val title: String,
    val releaseYear: Int,
    val genre: String,
    val duration: Int,
    val type: String,
)
