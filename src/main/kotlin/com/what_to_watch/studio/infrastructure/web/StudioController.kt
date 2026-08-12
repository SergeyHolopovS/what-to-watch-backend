package com.what_to_watch.studio.infrastructure.web

import com.what_to_watch.configs.STUDIOS_TAG
import com.what_to_watch.studio.application.usecase.getall.GetAllStudiosUseCase
import com.what_to_watch.studio.infrastructure.mappers.StudioMapper
import com.what_to_watch.studio.infrastructure.web.dto.response.StudioDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = STUDIOS_TAG)
@RestController
@RequestMapping("/studios")
class StudioController(
    private val getAllStudiosUseCase: GetAllStudiosUseCase,
    private val studioMapper: StudioMapper,
) {

    @Operation(summary = "Список всех студий")
    @GetMapping
    fun getAllStudios(): List<StudioDto> =
        studioMapper.toDto(getAllStudiosUseCase.execute())

}
