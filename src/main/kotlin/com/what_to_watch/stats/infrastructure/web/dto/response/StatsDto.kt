package com.what_to_watch.stats.infrastructure.web.dto.response

data class StatsDto(
    val ratedFromOneToFive: Long,
    val averageRating: Double?,
    val totalCartoons: Long,
    val totalSeries: Long,
)
