package com.what_to_watch.catalog.application.usecase.search

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.catalog.infrastructure.web.dto.response.CatalogContentType
import com.what_to_watch.catalog.infrastructure.web.dto.response.CatalogItemDto
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

    fun execute(query: String?, studioIdOrName: String?): List<CatalogItemDto> {
        val studioId = resolveStudioParam(studioIdOrName)

        val cartoons = cartoonRepository.search(query, studioId).map {
            CatalogItemDto(
                id = it.id,
                title = it.title,
                releaseYear = it.releaseYear,
                rating = it.rating,
                genre = it.genre,
                duration = it.duration,
                type = it.type,
                studioId = it.studioId,
                createdAt = it.createdAt,
                contentType = CatalogContentType.CARTOON,
            )
        }
        val series = seriesRepository.search(query, studioId).map {
            CatalogItemDto(
                id = it.id,
                title = it.title,
                releaseYear = it.releaseYear,
                rating = it.rating,
                genre = it.genre,
                duration = it.duration,
                type = it.type,
                studioId = it.studioId,
                createdAt = it.createdAt,
                contentType = CatalogContentType.SERIES,
            )
        }

        return (cartoons + series).sortedByDescending { it.createdAt }
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

}
