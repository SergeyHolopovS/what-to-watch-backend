package com.what_to_watch.stats.application.usecase.getstats

data class GetStatsResult(
    val ratedFromOneToFive: Long,
    val averageRating: Double?,
    val totalCartoons: Long,
    val totalSeries: Long,
)
