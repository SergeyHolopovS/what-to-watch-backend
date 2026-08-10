package com.what_to_watch.series.application.usecase.getall

import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import org.springframework.stereotype.Component

@Component
class GetAllSeriesUseCase (
    val seriesRepository: SeriesRepository
) {

    fun execute(): List<Series> = seriesRepository.getAllSeries()

}
