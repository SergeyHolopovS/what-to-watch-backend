package com.what_to_watch.configs

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class WhitelistConfig(
    @Value($$"${auth.whitelist.discord-ids:}")
    rawDiscordIds: String,
) {

    /**
     * Список пуст -> не пускаем никого: вайтлист по умолчанию закрыт,
     * иначе забытая настройка молча открывала бы вход всем.
     */
    val discordIds: Set<Long> = rawDiscordIds
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { it.toLong() }
        .toSet()

}
