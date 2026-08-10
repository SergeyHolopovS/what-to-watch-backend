package com.what_to_watch.series.application.usecase.getrandom

import com.what_to_watch.exceptions.series.NoSeriesToWatchException
import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import org.springframework.stereotype.Component

@Component
class GetRandomSeriesUseCase (
    val seriesRepository: SeriesRepository
) {

    fun execute(): Series =
        seriesRepository.getRandomUnratedSeries()
            ?: throw NoSeriesToWatchException()

}
