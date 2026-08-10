package com.what_to_watch.tokens.infrastructure.schedule

import com.what_to_watch.tokens.domain.repository.RefreshTokenRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class RefreshTokenCleanupScheduler(
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    private val logger = KotlinLogging.logger {}

    /**
     * Чистит сессии, у которых истёк срок жизни refresh токена: без этого таблица
     * растёт бесконечно, а протухшие записи всё равно не проходят проверку.
     */
    @Scheduled(
        fixedRateString = $$"${jwt.schedule.refresh-autodelete-rate}",
        timeUnit = TimeUnit.HOURS,
    )
    fun deleteExpiredTokens() {
        logger.info { "Удаление устаревших refresh токенов" }
        refreshTokenRepository.deleteExpiredTokens()
    }

}
