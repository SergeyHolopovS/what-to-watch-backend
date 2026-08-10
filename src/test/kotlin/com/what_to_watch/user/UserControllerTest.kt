package com.what_to_watch.user

import com.what_to_watch.user.domain.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `без авторизации - 401`() {
        mockMvc.get("/users/me").andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `возвращает id и url аватара текущего пользователя`() {
        val created = userRepository.addUser(
            name = "avatar-test-user",
            passwordHash = null,
            discordId = 555L,
            avatarUrl = "https://cdn.discordapp.com/avatars/555/hash.png",
        )

        mockMvc.get("/users/me") {
            with(user(created))
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(created.id.toString()) }
            jsonPath("$.avatarUrl") { value("https://cdn.discordapp.com/avatars/555/hash.png") }
        }
    }

}
