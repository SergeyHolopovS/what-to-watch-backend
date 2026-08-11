package com.what_to_watch.series.application.usecase.create

import com.what_to_watch.exceptions.series.SeriesAlreadyExistsException
import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import org.springframework.stereotype.Component

@Component
class CreateSeriesUseCase (
    val seriesRepository: SeriesRepository
) {

    fun execute(command: CreateSeriesCommand): Series {
        if (seriesRepository.existsByTitle(command.title)) {
            throw SeriesAlreadyExistsException(command.title)
        }
        return seriesRepository.addSeries(
            command.title, command.releaseYear, command.genre, command.duration, command.type,
        )
    }

}
