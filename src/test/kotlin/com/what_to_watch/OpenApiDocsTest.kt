package com.what_to_watch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocsTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    private fun apiDocs(): String =
        mockMvc.get("/v3/api-docs")
            .andExpect { status { isOk() } }
            .andReturn()
            .response
            .contentAsString

    @Test
    fun `в документации есть все эндпоинты`() {
        val docs = apiDocs()

        listOf(
            "/auth",
            "/auth/callback",
            "/refresh",
            "/users/me",
            "/cartoons",
            "/cartoons/search",
            "/cartoons/random",
            "/cartoons/{id}",
            "/cartoons/{id}/rating",
            "/series",
            "/series/search",
            "/series/random",
            "/series/{id}",
            "/series/{id}/rating",
        ).forEach { path ->
            assertTrue(docs.contains("\"$path\""), "в документации нет пути $path")
        }
    }

    @Test
    fun `эндпоинты разложены по категориям и описаны`() {
        val docs = apiDocs()

        listOf("Авторизация", "Пользователь", "Токены", "Мультфильмы", "Сериалы").forEach { tag ->
            assertTrue(docs.contains(tag), "в документации нет категории $tag")
        }
        listOf(
            "Случайный неоценённый мультфильм",
            "Оценить сериал",
            "Обновить пару токенов",
            "Колбэк Discord: проверка вайтлиста и выдача токенов",
            "Краткая информация о текущем пользователе",
            "Поиск мультфильмов по названию",
            "Поиск сериалов по названию",
        ).forEach { summary ->
            assertTrue(docs.contains(summary), "в документации нет описания \"$summary\"")
        }
    }

    @Test
    fun `описаны схемы авторизации по cookie и bearer`() {
        val docs = apiDocs()

        assertTrue(docs.contains("cookieAuth"), "нет схемы cookieAuth")
        assertTrue(docs.contains("bearerAuth"), "нет схемы bearerAuth")
        assertTrue(docs.contains("access_token"), "cookie схема не ссылается на access_token")
    }

    @Test
    fun `swagger ui доступен без авторизации`() {
        mockMvc.get("/swagger-ui/index.html").andExpect { status { isOk() } }
    }

}
