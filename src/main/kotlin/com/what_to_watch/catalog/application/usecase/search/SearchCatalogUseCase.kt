package com.what_to_watch.catalog.application.usecase.search

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.catalog.infrastructure.web.dto.response.CatalogItemDto
import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import com.what_to_watch.studio.application.usecase.resolve.ResolveStudioUseCase
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * studioId принимает как id существующей студии, так и произвольную строку: если строка не
 * парсится в UUID, она трактуется как название и по ней создаётся новая студия (см. ResolveStudioUseCase).
 */
@Component
class SearchCatalogUseCase(
    private val cartoonRepository: CartoonRepository,
    private val seriesRepository: SeriesRepository,
    private val resolveStudioUseCase: ResolveStudioUseCase,
) {

    fun searchSeries(query: String?, studioIdOrName: String?): List<CatalogItemDto> {
        val studioId = resolveStudioParam(studioIdOrName)
        return seriesRepository.search(query, studioId)
            .map { it.toCatalogItemDto() }
            .sortedByDescending { it.createdAt }
    }

    fun searchCartoons(query: String?, studioIdOrName: String?): List<CatalogItemDto> {
        val studioId = resolveStudioParam(studioIdOrName)
        return cartoonRepository.search(query, studioId)
            .map { it.toCatalogItemDto() }
            .sortedByDescending { it.createdAt }
    }

    private fun resolveStudioParam(param: String?): UUID? {
        if (param.isNullOrBlank()) return null
        val studioId = runCatching { UUID.fromString(param) }.getOrNull()
        return if (studioId != null) {
            resolveStudioUseCase.execute(studioId, null)
        } else {
            resolveStudioUseCase.execute(null, param)
        }
    }

    private fun Series.toCatalogItemDto() = CatalogItemDto(
        id = id,
        title = title,
        releaseYear = releaseYear,
        rating = rating,
        genre = genre,
        duration = duration,
        type = type,
        studioId = studioId,
        createdAt = createdAt,
    )

    private fun Cartoon.toCatalogItemDto() = CatalogItemDto(
        id = id,
        title = title,
        releaseYear = releaseYear,
        rating = rating,
        genre = genre,
        duration = duration,
        type = type,
        studioId = studioId,
        createdAt = createdAt,
    )

}
