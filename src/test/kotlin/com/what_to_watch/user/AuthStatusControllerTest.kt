package com.what_to_watch.user

import com.what_to_watch.tokens.domain.repository.RefreshTokenRepository
import com.what_to_watch.tokens.domain.service.TokenService
import com.what_to_watch.user.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthStatusControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var tokenService: TokenService

    @Autowired
    private lateinit var refreshTokenRepository: RefreshTokenRepository

    @Test
    fun `без токена - 401`() {
        mockMvc.get("/auth/status")
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `с валидным access токеном - 204`() {
        val user = userRepository.addUser(
            name = "auth-status-tester-${System.nanoTime()}",
            passwordHash = null,
            discordId = System.nanoTime(),
            avatarUrl = "https://cdn.discordapp.com/embed/avatars/0.png",
        )
        val tokens = tokenService.generateTokenPair(user)
        refreshTokenRepository.save(tokens.refreshToken, user, tokens.tokenPairId)
        val accessToken = tokens.accessToken

        mockMvc.get("/auth/status") {
            header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        }.andExpect { status { isNoContent() } }
    }

}
