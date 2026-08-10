package com.what_to_watch.tokens.infrastructure.web

import com.what_to_watch.configs.TOKENS_TAG
import com.what_to_watch.exceptions.auth.RefreshTokenInvalidException
import com.what_to_watch.tokens.application.usecase.refresh.RefreshCommand
import com.what_to_watch.tokens.application.usecase.refresh.RefreshUseCase
import com.what_to_watch.tokens.infrastructure.web.cookies.TokenCookieService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = TOKENS_TAG)
@Validated
@RestController
class TokensController(
    private val refreshUseCase: RefreshUseCase,
    private val tokenCookieService: TokenCookieService,
) {

    @Operation(
        summary = "Обновить пару токенов",
        description = "Оба токена читаются из cookie, тело запроса не нужно. " +
            "Требует просроченный access и живой refresh из одной пары; " +
            "старая сессия гасится, новые cookie ставятся в ответе.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Новые cookie установлены"),
        ApiResponse(responseCode = "400", description = "Access токен ещё активен"),
        ApiResponse(responseCode = "401", description = "Refresh невалиден, неактивен или из другой пары"),
    )
    @PostMapping("/refresh")
    fun refresh(request: HttpServletRequest): ResponseEntity<Void> {
        // Токены приходят из cookie, а не из тела: клиент их не видит
        val accessToken = tokenCookieService.readAccessToken(request)
            ?: throw RefreshTokenInvalidException()
        val refreshToken = tokenCookieService.readRefreshToken(request)
            ?: throw RefreshTokenInvalidException()

        val result = refreshUseCase.execute(
            RefreshCommand(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )

        return ResponseEntity
            .noContent()
            .header(
                HttpHeaders.SET_COOKIE,
                tokenCookieService.accessTokenCookie(result.accessToken).toString(),
            )
            .header(
                HttpHeaders.SET_COOKIE,
                tokenCookieService.refreshTokenCookie(result.refreshToken).toString(),
            )
            .build()
    }

}
