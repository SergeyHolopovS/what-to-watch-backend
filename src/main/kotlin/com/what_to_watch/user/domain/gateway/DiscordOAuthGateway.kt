package com.what_to_watch.user.domain.gateway

import com.what_to_watch.user.domain.model.DiscordAccount

interface DiscordOAuthGateway {

    /**
     * Ссылка, на которую переадресуется пользователь для входа через Discord.
     * [state] возвращается Discord'ом в колбэк и защищает от CSRF.
     */
    fun buildAuthorizationUrl(state: String): String

    /**
     * Обменивает код авторизации на токен и запрашивает по нему данные аккаунта.
     */
    fun exchangeCodeForAccount(code: String): DiscordAccount

}
