package com.what_to_watch.stats.infrastructure.web

import com.what_to_watch.configs.STATS_TAG
import com.what_to_watch.stats.application.usecase.getstats.GetStatsUseCase
import com.what_to_watch.stats.infrastructure.web.dto.response.StatsDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = STATS_TAG)
@RestController
@RequestMapping("/stats")
class StatsController(
    private val getStatsUseCase: GetStatsUseCase,
) {

    @Operation(
        summary = "Статистика по каталогу",
        description = "Количество мультфильмов и сериалов вместе с оценкой от 1 до 5, " +
            "средняя оценка среди всех оценённых, и общее количество мультфильмов и сериалов по отдельности.",
    )
    @GetMapping
    fun getStats(): StatsDto {
        val result = getStatsUseCase.execute()
        return StatsDto(
            ratedFromOneToFive = result.ratedFromOneToFive,
            averageRating = result.averageRating,
            totalCartoons = result.totalCartoons,
            totalSeries = result.totalSeries,
        )
    }

}
