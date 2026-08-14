package com.what_to_watch.user.infrastructure.web

import com.what_to_watch.configs.AUTH_TAG
import com.what_to_watch.configs.FrontendConfig
import com.what_to_watch.exceptions.user.DiscordAuthException
import com.what_to_watch.tokens.infrastructure.web.cookies.TokenCookieService
import com.what_to_watch.user.application.usecase.discordauth.AuthenticateViaDiscordCommand
import com.what_to_watch.user.application.usecase.discordauth.AuthenticateViaDiscordUseCase
import com.what_to_watch.user.domain.gateway.DiscordOAuthGateway
import com.what_to_watch.configs.BEARER_AUTH
import com.what_to_watch.configs.COOKIE_AUTH
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityRequirements
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI
import java.security.SecureRandom
import java.util.Base64

@Tag(name = AUTH_TAG)
@RestController
@RequestMapping("/auth")
class AuthController(
    private val discordOAuthGateway: DiscordOAuthGateway,
    private val authenticateViaDiscordUseCase: AuthenticateViaDiscordUseCase,
    private val tokenCookieService: TokenCookieService,
    private val frontendConfig: FrontendConfig,
) {

    private val random = SecureRandom()

    @Operation(
        summary = "Начать вход через Discord",
        description = "Отдаёт 302 на страницу авторизации Discord. " +
            "Одноразовый state сохраняется в сессии и проверяется в колбэке. " +
            "Открывать в браузере, а не через Swagger UI.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "302", description = "Редирект на discord.com"),
    )
    @GetMapping
    fun authorize(session: HttpSession): ResponseEntity<Void> {
        val state = generateState()
        session.setAttribute(STATE_ATTRIBUTE, state)
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(discordOAuthGateway.buildAuthorizationUrl(state)))
            .build()
    }

    @Operation(
        summary = "Колбэк Discord: проверка вайтлиста и выдача токенов",
        description = "Обменивает код на данные аккаунта Discord, проверяет discordId " +
            "по вайтлисту и только после этого создаёт аккаунт при первом входе. " +
            "Пара токенов уходит в HttpOnly cookie, после чего браузер редиректится на " +
            "frontend.redirect-uri. Данные пользователя доступны там через GET /users/me. " +
            "Вызывается самим Discord, вручную дёргать не нужно.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "302", description = "Cookie с токенами установлены, редирект на фронтенд"),
        ApiResponse(responseCode = "401", description = "Неверный state или ошибка обмена кода"),
        ApiResponse(responseCode = "403", description = "discordId отсутствует в вайтлисте"),
    )
    @GetMapping("/callback")
    fun callback(
        @RequestParam code: String,
        @RequestParam state: String,
        session: HttpSession,
    ): ResponseEntity<Void> {
        verifyState(state, session)

        val result = authenticateViaDiscordUseCase.execute(
            AuthenticateViaDiscordCommand(code = code)
        )

        // Токены отдаём только в HttpOnly cookie, в теле их нет — до них не может
        // добраться javascript на странице. Сами данные пользователя фронтенд
        // забирает отдельным вызовом GET /users/me после редиректа.
        return ResponseEntity
            .status(HttpStatus.FOUND)
            .location(URI.create(frontendConfig.redirectUri))
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

    @Operation(
        summary = "Проверить, авторизован ли пользователь",
        description = "Ничего не читает из бд — только проверяет access токен из cookie/заголовка " +
            "через тот же JwtFilter, что и остальные защищённые эндпоинты. " +
            "204 значит токен валиден, 401 — нет или его вовсе нет. Для данных пользователя " +
            "используйте GET /users/me.",
    )
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Пользователь авторизован"),
        ApiResponse(responseCode = "401", description = "Не авторизован"),
    )
    @SecurityRequirements(
        SecurityRequirement(name = COOKIE_AUTH),
        SecurityRequirement(name = BEARER_AUTH),
    )
    @GetMapping("/status")
    fun status(): ResponseEntity<Void> = ResponseEntity.noContent().build()

    private fun generateState(): String {
        val bytes = ByteArray(STATE_BYTES)
        random.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    /**
     * Одноразовая проверка state: он выдаётся в /auth, живёт в сессии и гасится
     * при первом же колбэке, чтобы код нельзя было подставить чужому пользователю.
     */
    private fun verifyState(state: String, session: HttpSession) {
        val expected = session.getAttribute(STATE_ATTRIBUTE) as? String
        session.removeAttribute(STATE_ATTRIBUTE)
        if (expected == null || expected != state) {
            throw DiscordAuthException("Некорректный state авторизации")
        }
    }

    private companion object {
        const val STATE_ATTRIBUTE = "discord_oauth_state"
        const val STATE_BYTES = 32
    }

}
