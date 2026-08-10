package com.what_to_watch.user.infrastructure.discord

import com.what_to_watch.exceptions.user.DiscordAuthException
import com.what_to_watch.user.domain.gateway.DiscordOAuthGateway
import com.what_to_watch.user.domain.model.DiscordAccount
import com.what_to_watch.user.infrastructure.discord.dto.DiscordTokenResponse
import com.what_to_watch.user.infrastructure.discord.dto.DiscordUserResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.util.UriComponentsBuilder

@Component
class DiscordOAuthGatewayImpl(
    private val properties: DiscordOAuthProperties,
    restClientBuilder: RestClient.Builder,
) : DiscordOAuthGateway {

    private val logger = KotlinLogging.logger {}

    private val restClient: RestClient = restClientBuilder.build()

    override fun buildAuthorizationUrl(state: String): String =
        UriComponentsBuilder.fromUriString(properties.authorizationUri)
            .queryParam("client_id", properties.clientId)
            .queryParam("redirect_uri", properties.redirectUri)
            .queryParam("response_type", "code")
            .queryParam("scope", properties.scope)
            .queryParam("state", state)
            .build()
            .encode()
            .toUriString()

    override fun exchangeCodeForAccount(code: String): DiscordAccount {
        val token = exchangeCode(code)
        val discordUser = fetchUser(token.accessToken)
        val discordId = discordUser.id.toLongOrNull()
            ?: throw DiscordAuthException("Discord вернул некорректный id пользователя")
        return DiscordAccount(
            discordId = discordId,
            username = discordUser.globalName ?: discordUser.username,
            avatarUrl = buildAvatarUrl(discordUser.id, discordId, discordUser.avatar),
        )
    }

    /**
     * https://discord.com/developers/docs/reference#image-formatting.
     * Без кастомного аватара Discord отдаёт один из 6 дефолтных по индексу (id >> 22) % 6.
     */
    private fun buildAvatarUrl(id: String, discordId: Long, avatarHash: String?): String =
        if (avatarHash != null) {
            val extension = if (avatarHash.startsWith("a_")) "gif" else "png"
            "https://cdn.discordapp.com/avatars/$id/$avatarHash.$extension"
        } else {
            val defaultIndex = (discordId shr 22) % 6
            "https://cdn.discordapp.com/embed/avatars/$defaultIndex.png"
        }

    private fun exchangeCode(code: String): DiscordTokenResponse {
        val body = LinkedMultiValueMap<String, String>().apply {
            add("client_id", properties.clientId)
            add("client_secret", properties.clientSecret)
            add("grant_type", "authorization_code")
            add("code", code)
            add("redirect_uri", properties.redirectUri)
        }
        return try {
            restClient.post()
                .uri(properties.tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(DiscordTokenResponse::class.java)
                ?: throw DiscordAuthException("Discord не вернул токен доступа")
        } catch (e: RestClientException) {
            logger.warn(e) { "Не удалось обменять код авторизации Discord" }
            throw DiscordAuthException("Не удалось обменять код авторизации Discord")
        }
    }

    private fun fetchUser(accessToken: String): DiscordUserResponse =
        try {
            restClient.get()
                .uri(properties.userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(DiscordUserResponse::class.java)
                ?: throw DiscordAuthException("Discord не вернул данные пользователя")
        } catch (e: RestClientException) {
            logger.warn(e) { "Не удалось получить данные пользователя Discord" }
            throw DiscordAuthException("Не удалось получить данные пользователя Discord")
        }

}
