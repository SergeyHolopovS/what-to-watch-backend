package com.what_to_watch.seed

import com.what_to_watch.series.domain.repository.SeriesRepository
import com.what_to_watch.studio.domain.repository.StudioRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class SeriesSeeder(
    private val seriesRepository: SeriesRepository,
    private val studioRepository: StudioRepository,
) : CommandLineRunner {

    private val logger = KotlinLogging.logger {}

    override fun run(vararg args: String) {
        val added = FAKE_SERIES.count { fake ->
            if (seriesRepository.existsByTitle(fake.title)) {
                false
            } else {
                val studioId = studioRepository.addStudio(fake.studio).id
                seriesRepository.addSeries(
                    fake.title, fake.releaseYear, fake.genre, fake.duration, fake.type, studioId,
                )
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
        val studio: String,
    )

    private companion object {
        val FAKE_SERIES = listOf(
            FakeSeries("Во все тяжкие", 2008, "Драма", 47, "Сериал", "AMC"),
            FakeSeries("Игра престолов", 2011, "Фэнтези", 57, "Сериал", "HBO"),
            FakeSeries("Друзья", 1994, "Комедия", 22, "Сериал", "NBC"),
            FakeSeries("Офис", 2005, "Комедия", 22, "Сериал", "NBC"),
            FakeSeries("Очень странные дела", 2016, "Фантастика", 51, "Сериал", "Netflix"),
            FakeSeries("Шерлок", 2010, "Детектив", 88, "Мини-сериал", "BBC"),
            FakeSeries("Чёрное зеркало", 2011, "Фантастика", 60, "Антология", "Netflix"),
            FakeSeries("Рик и Морти", 2013, "Комедия", 22, "Сериал", "Adult Swim"),
            FakeSeries("Бумажный дом", 2017, "Драма", 50, "Сериал", "Netflix"),
            FakeSeries("Мандалорец", 2019, "Фантастика", 40, "Сериал", "Lucasfilm"),
        )
    }

}
