package com.what_to_watch.user.infrastructure.security

import com.what_to_watch.configs.WhitelistConfig
import com.what_to_watch.user.domain.service.AccessWhitelist
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class DiscordAccessWhitelist(
    private val whitelistConfig: WhitelistConfig,
) : AccessWhitelist {

    private val logger = KotlinLogging.logger {}

    override fun isAllowed(discordId: Long): Boolean =
        discordId in whitelistConfig.discordIds

    @PostConstruct
    fun warnIfEmpty() {
        if (whitelistConfig.discordIds.isEmpty()) {
            logger.warn {
                "Вайтлист auth.whitelist.discord-ids пуст — вход через Discord закрыт для всех"
            }
        }
    }

}
