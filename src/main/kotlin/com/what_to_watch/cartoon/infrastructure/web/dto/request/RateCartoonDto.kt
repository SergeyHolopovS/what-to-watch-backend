package com.what_to_watch.cartoon.infrastructure.web.dto.request

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class RateCartoonDto(

    @field:Min(value = 1, message = "Оценка не может быть меньше 1")
    @field:Max(value = 10, message = "Оценка не может быть больше 10")
    val rating: Int,

)
