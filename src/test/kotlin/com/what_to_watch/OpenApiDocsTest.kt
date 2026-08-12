package com.what_to_watch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import tools.jackson.databind.ObjectMapper
import kotlin.test.Test
import kotlin.test.assertTrue

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpenApiDocsTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

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
            "/stats",
            "/cartoons",
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

        listOf("Авторизация", "Пользователь", "Токены", "Мультфильмы", "Сериалы", "Статистика").forEach { tag ->
            assertTrue(docs.contains(tag), "в документации нет категории $tag")
        }
        listOf(
            "Случайный неоценённый мультфильм",
            "Оценить сериал",
            "Обновить пару токенов",
            "Колбэк Discord: проверка вайтлиста и выдача токенов",
            "Краткая информация о текущем пользователе",
            "Поиск сериалов по названию",
            "Статистика по каталогу",
        ).forEach { summary ->
            assertTrue(docs.contains(summary), "в документации нет описания \"$summary\"")
        }
    }

    @Test
    fun `схемы мультфильма и сериала содержат genre, duration и type`() {
        val schemas = objectMapper.readTree(apiDocs()).get("components").get("schemas")

        listOf("CartoonDto", "CreateCartoonDto", "SeriesDto", "CreateSeriesDto").forEach { schemaName ->
            val properties = schemas.get(schemaName)?.get("properties")
            requireNotNull(properties) { "в документации нет схемы $schemaName" }
            listOf("genre", "duration", "type").forEach { field ->
                assertTrue(properties.has(field), "в схеме $schemaName нет поля $field")
            }
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
