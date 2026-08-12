package com.what_to_watch.seed

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.studio.domain.repository.StudioRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class CartoonSeeder(
    private val cartoonRepository: CartoonRepository,
    private val studioRepository: StudioRepository,
) : CommandLineRunner {

    private val logger = KotlinLogging.logger {}

    override fun run(vararg args: String) {
        val added = FAKE_CARTOONS.count { fake ->
            if (cartoonRepository.existsByTitle(fake.title)) {
                false
            } else {
                val studioId = studioRepository.addStudio(fake.studio).id
                cartoonRepository.addCartoon(
                    fake.title, fake.releaseYear, fake.genre, fake.duration, fake.type, studioId,
                )
                true
            }
        }
        if (added > 0) {
            logger.info { "Добавлено фейковых мультфильмов: $added" }
        }
    }

    private data class FakeCartoon(
        val title: String,
        val releaseYear: Int,
        val genre: String,
        val duration: Int,
        val type: String,
        val studio: String,
    )

    private companion object {
        val FAKE_CARTOONS = listOf(
            FakeCartoon("Ну, погоди!", 1969, "Комедия", 10, "Короткометражный", "Союзмультфильм"),
            FakeCartoon("Простоквашино", 1978, "Комедия", 20, "Короткометражный", "Союзмультфильм"),
            FakeCartoon("Ёжик в тумане", 1975, "Драма", 10, "Короткометражный", "Союзмультфильм"),
            FakeCartoon("Малыш и Карлсон", 1968, "Комедия", 19, "Короткометражный", "Союзмультфильм"),
            FakeCartoon("Ледниковый период", 2002, "Приключения", 81, "Полнометражный", "Blue Sky Studios"),
            FakeCartoon("Шрек", 2001, "Комедия", 90, "Полнометражный", "DreamWorks Animation"),
            FakeCartoon("История игрушек", 1995, "Приключения", 81, "Полнометражный", "Pixar"),
            FakeCartoon("В поисках Немо", 2003, "Приключения", 100, "Полнометражный", "Pixar"),
            FakeCartoon("Головоломка", 2015, "Драма", 95, "Полнометражный", "Pixar"),
            FakeCartoon("Тайна Коко", 2017, "Драма", 105, "Полнометражный", "Pixar"),
        )
    }

}
