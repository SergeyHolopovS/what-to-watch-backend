package com.what_to_watch.tokens.infrastructure.security.filters

import com.what_to_watch.tokens.domain.repository.RefreshTokenRepository
import com.what_to_watch.tokens.domain.service.TokenService
import com.what_to_watch.tokens.infrastructure.web.cookies.TokenCookieService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.Collections
import java.util.UUID

@Component
class JwtFilter(
    private val jwtService: TokenService,
    private val userDetailsService: UserDetailsService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenCookieService: TokenCookieService,
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            // Если контекст уже установлен -> идём дальше по фильтрам
            if (
                SecurityContextHolder.getContext().authentication != null
            ) throw RuntimeException()

            // Вытаскиваем токен из заголовка или из cookie
            val token = extractToken(request)

            // Если токена нет -> идём дальше по фильтрам
            if (token == "") throw RuntimeException()

            // Вытаскиваем из токена id пары
            // Если tokenPairId == null -> access токен expired
            val tokenPairId: String = jwtService.extractTokenPairId(token) ?: throw RuntimeException()

            // Если токен неактивен == пользователь забанен или заблокировал сессию
            if (!refreshTokenRepository.isRefreshTokenActiveByTokenPairId(tokenPairId)) throw RuntimeException()

            // Вытаскиваем из jwt id
            val id: UUID = jwtService.extractId(token)

            // Ищем пользователя по id
            val user: UserDetails = userDetailsService.loadUserByUsername(id.toString())

            // Создаём токен для установки авторизации в контекст холдере
            val authenticationToken = UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.emptyList()
            )

            // Устанавливаем авторизацию в контекст
            SecurityContextHolder.getContext().authentication = authenticationToken
        } catch (_: Exception) {}

        // Прокидываем фильтры дальше
        filterChain.doFilter(request, response)
    }

    /**
     * Браузер ходит с HttpOnly cookie, сторонние клиенты — с заголовком Authorization.
     */
    private fun extractToken(request: HttpServletRequest): String
        = request.getHeader(HttpHeaders.AUTHORIZATION)?.substringAfter("Bearer ")
            ?: tokenCookieService.readAccessToken(request)
            ?: ""

}
