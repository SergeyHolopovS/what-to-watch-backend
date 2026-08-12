package com.what_to_watch.series.application.usecase.create

import com.what_to_watch.exceptions.series.SeriesAlreadyExistsException
import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import com.what_to_watch.studio.application.usecase.resolve.ResolveStudioUseCase
import org.springframework.stereotype.Component

@Component
class CreateSeriesUseCase (
    val seriesRepository: SeriesRepository,
    val resolveStudioUseCase: ResolveStudioUseCase,
) {

    fun execute(command: CreateSeriesCommand): Series {
        if (seriesRepository.existsByTitle(command.title)) {
            throw SeriesAlreadyExistsException(command.title)
        }
        val studioId = resolveStudioUseCase.execute(command.studioId, command.studioName)
        return seriesRepository.addSeries(
            command.title, command.releaseYear, command.genre, command.duration, command.type, studioId,
        )
    }

}
