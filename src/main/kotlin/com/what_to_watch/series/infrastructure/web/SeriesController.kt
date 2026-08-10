package com.what_to_watch.series.infrastructure.web

import com.what_to_watch.series.application.usecase.create.CreateSeriesCommand
import com.what_to_watch.series.application.usecase.create.CreateSeriesUseCase
import com.what_to_watch.series.application.usecase.delete.DeleteSeriesUseCase
import com.what_to_watch.series.application.usecase.getall.GetAllSeriesUseCase
import com.what_to_watch.series.application.usecase.getrandom.GetRandomSeriesUseCase
import com.what_to_watch.series.application.usecase.rate.RateSeriesCommand
import com.what_to_watch.series.application.usecase.rate.RateSeriesUseCase
import com.what_to_watch.series.application.usecase.search.SearchSeriesUseCase
import com.what_to_watch.series.infrastructure.mappers.SeriesMapper
import com.what_to_watch.series.infrastructure.web.dto.request.CreateSeriesDto
import com.what_to_watch.series.infrastructure.web.dto.request.RateSeriesDto
import com.what_to_watch.configs.BEARER_AUTH
import com.what_to_watch.configs.COOKIE_AUTH
import com.what_to_watch.configs.SERIES_TAG
import com.what_to_watch.series.infrastructure.web.dto.response.SeriesDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = SERIES_TAG)
@Validated
@RestController
@RequestMapping("/series")
class SeriesController(
    private val createSeriesUseCase: CreateSeriesUseCase,
    private val rateSeriesUseCase: RateSeriesUseCase,
    private val getRandomSeriesUseCase: GetRandomSeriesUseCase,
    private val getAllSeriesUseCase: GetAllSeriesUseCase,
    private val deleteSeriesUseCase: DeleteSeriesUseCase,
    private val searchSeriesUseCase: SearchSeriesUseCase,
    private val seriesMapper: SeriesMapper,
) {

    @Operation(
        summary = "Добавить сериал",
        description = "Создаёт сериал с нулевым рейтингом. Название должно быть уникальным.",
    )
    @SecurityRequirements(
        SecurityRequirement(name = COOKIE_AUTH),
        SecurityRequirement(name = BEARER_AUTH),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createSeries(@Valid @RequestBody dto: CreateSeriesDto): SeriesDto {
        val command = CreateSeriesCommand(
            title = dto.title,
            releaseYear = dto.releaseYear,
        )
        return seriesMapper.toDto(createSeriesUseCase.execute(command))
    }

    @Operation(summary = "Список всех сериалов")
    @GetMapping
    fun getAllSeries(): List<SeriesDto> =
        seriesMapper.toDto(getAllSeriesUseCase.execute())

    @Operation(
        summary = "Поиск сериалов по названию",
        description = "Ищет по подстроке в title, без учёта регистра.",
    )
    @GetMapping("/search")
    fun searchSeries(@RequestParam @NotBlank title: String): List<SeriesDto> =
        seriesMapper.toDto(searchSeriesUseCase.execute(title))

    @Operation(
        summary = "Случайный неоценённый сериал",
        description = "Возвращает случайный сериал с рейтингом 0. " +
            "Если таких не осталось — 404.",
    )
    @GetMapping("/random")
    fun getRandomSeries(): SeriesDto =
        seriesMapper.toDto(getRandomSeriesUseCase.execute())

    @Operation(
        summary = "Оценить сериал",
        description = "Проставляет оценку от 1 до 10. После этого сериал " +
            "перестаёт попадать в случайную выдачу.",
    )
    @SecurityRequirements(
        SecurityRequirement(name = COOKIE_AUTH),
        SecurityRequirement(name = BEARER_AUTH),
    )
    @PatchMapping("/{id}/rating")
    fun rateSeries(
        @PathVariable id: UUID,
        @Valid @RequestBody dto: RateSeriesDto,
    ): SeriesDto {
        val command = RateSeriesCommand(
            id = id,
            rating = dto.rating,
        )
        return seriesMapper.toDto(rateSeriesUseCase.execute(command))
    }

    @Operation(summary = "Удалить сериал")
    @SecurityRequirements(
        SecurityRequirement(name = COOKIE_AUTH),
        SecurityRequirement(name = BEARER_AUTH),
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteSeries(@PathVariable id: UUID) {
        deleteSeriesUseCase.execute(id)
    }

}
