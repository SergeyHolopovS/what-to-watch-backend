package com.what_to_watch.stats.application.usecase.getstats

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.series.domain.repository.SeriesRepository
import org.springframework.stereotype.Component

@Component
class GetStatsUseCase(
    val cartoonRepository: CartoonRepository,
    val seriesRepository: SeriesRepository,
) {

    fun execute(): GetStatsResult {
        val cartoons = cartoonRepository.getAllCartoons()
        val series = seriesRepository.getAllSeries()
        val ratings = cartoons.map { it.rating } + series.map { it.rating }

        // 0 = не оценено, поэтому в среднюю оценку и её нет смысла включать
        val ratedRatings = ratings.filter { it > 0 }

        return GetStatsResult(
            ratedFromOneToFive = ratings.count { it in 1..5 }.toLong(),
            averageRating = ratedRatings.takeIf { it.isNotEmpty() }?.average(),
            totalCartoons = cartoons.size.toLong(),
            totalSeries = series.size.toLong(),
        )
    }

}
