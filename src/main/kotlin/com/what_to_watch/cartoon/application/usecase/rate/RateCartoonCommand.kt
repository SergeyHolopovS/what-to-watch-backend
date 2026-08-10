package com.what_to_watch.cartoon.application.usecase.rate

import java.util.UUID

data class RateCartoonCommand(
    val id: UUID,
    val rating: Int,
)
