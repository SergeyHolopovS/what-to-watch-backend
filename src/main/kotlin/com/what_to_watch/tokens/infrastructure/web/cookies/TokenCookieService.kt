package com.what_to_watch.tokens.infrastructure.web.cookies

import com.what_to_watch.configs.CookieConfig
import com.what_to_watch.configs.JwtConfig
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class TokenCookieService(
    private val jwtConfig: JwtConfig,
    private val cookieConfig: CookieConfig,
) {

    fun accessTokenCookie(token: String): ResponseCookie =
        build(
            name = cookieConfig.accessTokenName,
            value = token,
            path = ROOT_PATH,
            maxAge = Duration.ofSeconds(jwtConfig.accessTokenExpiration),
        )

    /**
     * Refresh кладём на путь /refresh: он нужен только этому эндпоинту,
     * и браузер не отправит его вместе с остальными запросами.
     */
    fun refreshTokenCookie(token: String): ResponseCookie =
        build(
            name = cookieConfig.refreshTokenName,
            value = token,
            path = cookieConfig.refreshPath,
            maxAge = Duration.ofSeconds(jwtConfig.refreshTokenExpiration),
        )

    fun readAccessToken(request: HttpServletRequest): String? =
        read(request, cookieConfig.accessTokenName)

    fun readRefreshToken(request: HttpServletRequest): String? =
        read(request, cookieConfig.refreshTokenName)

    private fun read(request: HttpServletRequest, name: String): String? =
        request.cookies
            ?.firstOrNull { it.name == name }
            ?.value
            ?.takeIf { it.isNotBlank() }

    private fun build(
        name: String,
        value: String,
        path: String,
        maxAge: Duration,
    ): ResponseCookie =
        ResponseCookie
            .from(name, value)
            .httpOnly(true)
            .secure(cookieConfig.secure)
            .sameSite(cookieConfig.sameSite)
            .path(path)
            .maxAge(maxAge)
            .build()

    private companion object {
        const val ROOT_PATH = "/"
    }

}
