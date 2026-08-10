package com.what_to_watch.series.application.usecase.rate

import java.util.UUID

data class RateSeriesCommand(
    val id: UUID,
    val rating: Int,
)
