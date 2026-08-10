package com.what_to_watch.series.application.usecase.rate

import com.what_to_watch.exceptions.series.SeriesNotFoundException
import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import org.springframework.stereotype.Component

@Component
class RateSeriesUseCase (
    val seriesRepository: SeriesRepository
) {

    fun execute(command: RateSeriesCommand): Series {
        val series = seriesRepository.getSeries(command.id)
            ?: throw SeriesNotFoundException(command.id)
        return seriesRepository.saveSeries(series.copy(rating = command.rating))
    }

}
