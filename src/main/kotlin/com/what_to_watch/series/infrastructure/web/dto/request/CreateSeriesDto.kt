package com.what_to_watch.series.infrastructure.web.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class CreateSeriesDto(

    @field:NotBlank(message = "Название не должно быть пустым")
    val title: String,

    @field:Min(value = 1900, message = "Год выпуска не может быть раньше 1900")
    @field:Max(value = 2100, message = "Год выпуска не может быть позже 2100")
    val releaseYear: Int,

)
