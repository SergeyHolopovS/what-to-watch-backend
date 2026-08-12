package com.what_to_watch.series.application.usecase.search

import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SearchSeriesUseCase (
    val seriesRepository: SeriesRepository
) {

    fun execute(query: String, studioId: UUID?): List<Series> = seriesRepository.search(query, studioId)

}
