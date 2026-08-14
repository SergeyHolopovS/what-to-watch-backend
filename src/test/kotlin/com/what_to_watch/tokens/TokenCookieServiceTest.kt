package com.what_to_watch.tokens

import com.what_to_watch.configs.CookieConfig
import com.what_to_watch.configs.JwtConfig
import com.what_to_watch.tokens.infrastructure.web.cookies.TokenCookieService
import jakarta.servlet.http.Cookie
import org.springframework.mock.web.MockHttpServletRequest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TokenCookieServiceTest {

    private val jwtConfig = JwtConfig(
        privateKey = "",
        publicKey = "",
        accessTokenExpiration = 900,
        refreshTokenExpiration = 864000,
        tokenPairIdClaim = "tokenPairId",
        tokenTypeClaim = "tokenType",
        accessTokenType = "access",
        refreshTokenType = "refresh",
    )

    private val cookieConfig = CookieConfig(
        accessTokenName = "access_token",
        refreshTokenName = "refresh_token",
        secure = true,
        sameSite = "Lax",
        refreshPath = "/refresh",
    )

    private val service = TokenCookieService(jwtConfig, cookieConfig)

    @Test
    fun `access cookie httpOnly, secure и живёт столько же, сколько refresh токен`() {
        val cookie = service.accessTokenCookie("token-value")

        assertEquals("access_token", cookie.name)
        assertEquals("token-value", cookie.value)
        assertTrue(cookie.isHttpOnly)
        assertTrue(cookie.isSecure)
        assertEquals("Lax", cookie.sameSite)
        assertEquals("/", cookie.path)
        // Cookie должна пережить истечение access-токена: /refresh читает её
        // именно после того, как токен внутри стал протухшим
        assertEquals(jwtConfig.refreshTokenExpiration, cookie.maxAge.seconds)
    }

    @Test
    fun `refresh cookie ограничен путём refresh`() {
        val cookie = service.refreshTokenCookie("token-value")

        assertEquals("refresh_token", cookie.name)
        assertTrue(cookie.isHttpOnly)
        assertEquals("/refresh", cookie.path)
        assertEquals(jwtConfig.refreshTokenExpiration, cookie.maxAge.seconds)
    }

    @Test
    fun `читает токены из запроса`() {
        val request = MockHttpServletRequest()
        request.setCookies(
            Cookie("access_token", "a"),
            Cookie("refresh_token", "r"),
        )

        assertEquals("a", service.readAccessToken(request))
        assertEquals("r", service.readRefreshToken(request))
    }

    @Test
    fun `нет cookie или пустое значение - null`() {
        assertNull(service.readAccessToken(MockHttpServletRequest()))

        val request = MockHttpServletRequest()
        request.setCookies(Cookie("access_token", ""))
        assertNull(service.readAccessToken(request))
    }

}
