package com.what_to_watch.configs

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityScheme
import io.swagger.v3.oas.annotations.security.SecuritySchemes
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info = Info(
        title = "What to watch API",
        version = "0.0.1",
        description = "Сервис случайного выбора мультфильма или сериала для просмотра " +
            "с последующей оценкой. Вход — только через Discord OAuth 2.0 по вайтлисту.",
    ),
    tags = [
        Tag(
            name = AUTH_TAG,
            description = "Вход через Discord OAuth 2.0 и выдача токенов",
        ),
        Tag(
            name = USERS_TAG,
            description = "Информация о текущем пользователе",
        ),
        Tag(
            name = TOKENS_TAG,
            description = "Обновление пары access/refresh токенов",
        ),
        Tag(
            name = CARTOONS_TAG,
            description = "Каталог мультфильмов: поиск по названию, случайный выбор и оценка",
        ),
        Tag(
            name = SERIES_TAG,
            description = "Каталог сериалов: поиск по названию, случайный выбор и оценка",
        ),
        Tag(
            name = STATS_TAG,
            description = "Сводная статистика по каталогу",
        ),
        Tag(
            name = STUDIOS_TAG,
            description = "Каталог студий, выпустивших мультфильмы и сериалы",
        ),
    ],
)
@SecuritySchemes(
    SecurityScheme(
        name = COOKIE_AUTH,
        type = SecuritySchemeType.APIKEY,
        `in` = SecuritySchemeIn.COOKIE,
        paramName = "access_token",
        description = "HttpOnly cookie, которую ставит GET /auth/callback",
    ),
    SecurityScheme(
        name = BEARER_AUTH,
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Альтернатива для не-браузерных клиентов: Authorization: Bearer <access token>",
    ),
)
class OpenApiConfig

const val AUTH_TAG = "Авторизация"
const val USERS_TAG = "Пользователь"
const val TOKENS_TAG = "Токены"
const val CARTOONS_TAG = "Мультфильмы"
const val SERIES_TAG = "Сериалы"
const val STATS_TAG = "Статистика"
const val STUDIOS_TAG = "Студии"

const val COOKIE_AUTH = "cookieAuth"
const val BEARER_AUTH = "bearerAuth"
