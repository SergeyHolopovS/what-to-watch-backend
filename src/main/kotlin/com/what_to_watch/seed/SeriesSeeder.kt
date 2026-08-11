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
        val added = FAKE_SERIES.count { fake ->
            if (seriesRepository.existsByTitle(fake.title)) {
                false
            } else {
                seriesRepository.addSeries(fake.title, fake.releaseYear, fake.genre, fake.duration, fake.type)
                true
            }
        }
        if (added > 0) {
            logger.info { "Добавлено фейковых сериалов: $added" }
        }
    }

    private data class FakeSeries(
        val title: String,
        val releaseYear: Int,
        val genre: String,
        val duration: Int,
        val type: String,
    )

    private companion object {
        val FAKE_SERIES = listOf(
            FakeSeries("Во все тяжкие", 2008, "Драма", 47, "Сериал"),
            FakeSeries("Игра престолов", 2011, "Фэнтези", 57, "Сериал"),
            FakeSeries("Друзья", 1994, "Комедия", 22, "Сериал"),
            FakeSeries("Офис", 2005, "Комедия", 22, "Сериал"),
            FakeSeries("Очень странные дела", 2016, "Фантастика", 51, "Сериал"),
            FakeSeries("Шерлок", 2010, "Детектив", 88, "Мини-сериал"),
            FakeSeries("Чёрное зеркало", 2011, "Фантастика", 60, "Антология"),
            FakeSeries("Рик и Морти", 2013, "Комедия", 22, "Сериал"),
            FakeSeries("Бумажный дом", 2017, "Драма", 50, "Сериал"),
            FakeSeries("Мандалорец", 2019, "Фантастика", 40, "Сериал"),
        )
    }

}
