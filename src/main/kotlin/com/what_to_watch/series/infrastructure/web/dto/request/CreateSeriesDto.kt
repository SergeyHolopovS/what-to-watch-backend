package com.what_to_watch.series.infrastructure.web.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive

data class CreateSeriesDto(

    @field:NotBlank(message = "Название не должно быть пустым")
    val title: String,

    @field:Min(value = 1900, message = "Год выпуска не может быть раньше 1900")
    @field:Max(value = 2100, message = "Год выпуска не может быть позже 2100")
    val releaseYear: Int,

    @field:NotBlank(message = "Жанр не должен быть пустым")
    val genre: String,

    @field:Positive(message = "Длительность должна быть больше 0")
    val duration: Int,

    @field:NotBlank(message = "Тип записи не должен быть пустым")
    val type: String,

)
