package com.what_to_watch.seed

import com.what_to_watch.series.domain.repository.SeriesRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class SeriesSeeder(
    private val seriesRepository: SeriesRepository,
) : CommandLineRunner {

    private val logger = KotlinLogging.logger {}

    override fun run(vararg args: String) {
        val added = FAKE_SERIES.count { (title, releaseYear) ->
            if (seriesRepository.existsByTitle(title)) {
                false
            } else {
                seriesRepository.addSeries(title, releaseYear)
                true
            }
        }
        if (added > 0) {
            logger.info { "Добавлено фейковых сериалов: $added" }
        }
    }

    private companion object {
        val FAKE_SERIES = listOf(
            "Во все тяжкие" to 2008,
            "Игра престолов" to 2011,
            "Друзья" to 1994,
            "Офис" to 2005,
            "Очень странные дела" to 2016,
            "Шерлок" to 2010,
            "Чёрное зеркало" to 2011,
            "Рик и Морти" to 2013,
            "Бумажный дом" to 2017,
            "Мандалорец" to 2019,
        )
    }

}
