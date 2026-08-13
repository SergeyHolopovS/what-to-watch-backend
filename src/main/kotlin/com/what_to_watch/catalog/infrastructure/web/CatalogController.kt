package com.what_to_watch.catalog.infrastructure.web

import com.what_to_watch.catalog.application.usecase.search.SearchCatalogUseCase
import com.what_to_watch.catalog.infrastructure.web.dto.response.CatalogItemDto
import com.what_to_watch.configs.CATALOG_TAG
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = CATALOG_TAG)
@RestController
@RequestMapping("/catalog")
class CatalogController(
    private val searchCatalogUseCase: SearchCatalogUseCase,
) {

    @Operation(
        summary = "Поиск по сериалам",
        description = "Список сериалов, отсортированный по дате добавления (сначала новые). " +
            "Фильтруется по подстроке в названии (query) и по студии (studioId): " +
            "если studioId не парсится как UUID, он трактуется как название студии и по нему создаётся новая студия.",
    )
    @GetMapping("/series")
    fun searchSeries(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) studioId: String?,
    ): List<CatalogItemDto> =
        searchCatalogUseCase.searchSeries(query, studioId)

    @Operation(
        summary = "Поиск по мультфильмам",
        description = "Список мультфильмов, отсортированный по дате добавления (сначала новые). " +
            "Фильтруется по подстроке в названии (query) и по студии (studioId): " +
            "если studioId не парсится как UUID, он трактуется как название студии и по нему создаётся новая студия.",
    )
    @GetMapping("/cartoons")
    fun searchCartoons(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) studioId: String?,
    ): List<CatalogItemDto> =
        searchCatalogUseCase.searchCartoons(query, studioId)

}
