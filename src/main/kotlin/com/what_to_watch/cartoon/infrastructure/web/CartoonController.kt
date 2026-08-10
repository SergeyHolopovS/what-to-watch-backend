package com.what_to_watch.cartoon.infrastructure.web

import com.what_to_watch.cartoon.application.usecase.create.CreateCartoonCommand
import com.what_to_watch.cartoon.application.usecase.create.CreateCartoonUseCase
import com.what_to_watch.cartoon.application.usecase.delete.DeleteCartoonUseCase
import com.what_to_watch.cartoon.application.usecase.getall.GetAllCartoonsUseCase
import com.what_to_watch.cartoon.application.usecase.getrandom.GetRandomCartoonUseCase
import com.what_to_watch.cartoon.application.usecase.rate.RateCartoonCommand
import com.what_to_watch.cartoon.application.usecase.rate.RateCartoonUseCase
import com.what_to_watch.cartoon.application.usecase.search.SearchCartoonsUseCase
import com.what_to_watch.cartoon.infrastructure.mappers.CartoonMapper
import com.what_to_watch.cartoon.infrastructure.web.dto.request.CreateCartoonDto
import com.what_to_watch.cartoon.infrastructure.web.dto.request.RateCartoonDto
import com.what_to_watch.cartoon.infrastructure.web.dto.response.CartoonDto
import com.what_to_watch.configs.BEARER_AUTH
import com.what_to_watch.configs.CARTOONS_TAG
import com.what_to_watch.configs.COOKIE_AUTH
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

@Tag(name = CARTOONS_TAG)
@Validated
@RestController
@RequestMapping("/cartoons")
class CartoonController(
    private val createCartoonUseCase: CreateCartoonUseCase,
    private val rateCartoonUseCase: RateCartoonUseCase,
    private val getRandomCartoonUseCase: GetRandomCartoonUseCase,
    private val getAllCartoonsUseCase: GetAllCartoonsUseCase,
    private val deleteCartoonUseCase: DeleteCartoonUseCase,
    private val searchCartoonsUseCase: SearchCartoonsUseCase,
    private val cartoonMapper: CartoonMapper,
) {

    @Operation(
        summary = "Добавить мультфильм",
        description = "Создаёт мультфильм с нулевым рейтингом. Название должно быть уникальным.",
    )
    @SecurityRequirements(
        SecurityRequirement(name = COOKIE_AUTH),
        SecurityRequirement(name = BEARER_AUTH),
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createCartoon(@Valid @RequestBody dto: CreateCartoonDto): CartoonDto {
        val command = CreateCartoonCommand(
            title = dto.title,
            releaseYear = dto.releaseYear,
        )
        return cartoonMapper.toDto(createCartoonUseCase.execute(command))
    }

    @Operation(summary = "Список всех мультфильмов")
    @GetMapping
    fun getAllCartoons(): List<CartoonDto> =
        cartoonMapper.toDto(getAllCartoonsUseCase.execute())

    @Operation(
        summary = "Поиск мультфильмов по названию",
        description = "Ищет по подстроке в title, без учёта регистра.",
    )
    @GetMapping("/search")
    fun searchCartoons(@RequestParam @NotBlank title: String): List<CartoonDto> =
        cartoonMapper.toDto(searchCartoonsUseCase.execute(title))

    @Operation(
        summary = "Случайный неоценённый мультфильм",
        description = "Возвращает случайный мультфильм с рейтингом 0. " +
            "Если таких не осталось — 404.",
    )
    @GetMapping("/random")
    fun getRandomCartoon(): CartoonDto =
        cartoonMapper.toDto(getRandomCartoonUseCase.execute())

    @Operation(
        summary = "Оценить мультфильм",
        description = "Проставляет оценку от 1 до 10. После этого мультфильм " +
            "перестаёт попадать в случайную выдачу.",
    )
    @SecurityRequirements(
        SecurityRequirement(name = COOKIE_AUTH),
        SecurityRequirement(name = BEARER_AUTH),
    )
    @PatchMapping("/{id}/rating")
    fun rateCartoon(
        @PathVariable id: UUID,
        @Valid @RequestBody dto: RateCartoonDto,
    ): CartoonDto {
        val command = RateCartoonCommand(
            id = id,
            rating = dto.rating,
        )
        return cartoonMapper.toDto(rateCartoonUseCase.execute(command))
    }

    @Operation(summary = "Удалить мультфильм")
    @SecurityRequirements(
        SecurityRequirement(name = COOKIE_AUTH),
        SecurityRequirement(name = BEARER_AUTH),
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCartoon(@PathVariable id: UUID) {
        deleteCartoonUseCase.execute(id)
    }

}
