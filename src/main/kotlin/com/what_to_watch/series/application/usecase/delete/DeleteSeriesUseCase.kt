package com.what_to_watch.series.application.usecase.delete

import com.what_to_watch.exceptions.series.SeriesNotFoundException
import com.what_to_watch.series.domain.repository.SeriesRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DeleteSeriesUseCase (
    val seriesRepository: SeriesRepository
) {

    fun execute(id: UUID) {
        seriesRepository.getSeries(id) ?: throw SeriesNotFoundException(id)
        seriesRepository.deleteSeries(id)
    }

}
