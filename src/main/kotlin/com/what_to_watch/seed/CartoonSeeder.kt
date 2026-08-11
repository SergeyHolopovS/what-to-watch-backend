package com.what_to_watch.seed

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("dev")
class CartoonSeeder(
    private val cartoonRepository: CartoonRepository,
) : CommandLineRunner {

    private val logger = KotlinLogging.logger {}

    override fun run(vararg args: String) {
        val added = FAKE_CARTOONS.count { fake ->
            if (cartoonRepository.existsByTitle(fake.title)) {
                false
            } else {
                cartoonRepository.addCartoon(fake.title, fake.releaseYear, fake.genre, fake.duration, fake.type)
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
    )

    private companion object {
        val FAKE_CARTOONS = listOf(
            FakeCartoon("Ну, погоди!", 1969, "Комедия", 10, "Короткометражный"),
            FakeCartoon("Простоквашино", 1978, "Комедия", 20, "Короткометражный"),
            FakeCartoon("Ёжик в тумане", 1975, "Драма", 10, "Короткометражный"),
            FakeCartoon("Малыш и Карлсон", 1968, "Комедия", 19, "Короткометражный"),
            FakeCartoon("Ледниковый период", 2002, "Приключения", 81, "Полнометражный"),
            FakeCartoon("Шрек", 2001, "Комедия", 90, "Полнометражный"),
            FakeCartoon("История игрушек", 1995, "Приключения", 81, "Полнометражный"),
            FakeCartoon("В поисках Немо", 2003, "Приключения", 100, "Полнометражный"),
            FakeCartoon("Головоломка", 2015, "Драма", 95, "Полнометражный"),
            FakeCartoon("Тайна Коко", 2017, "Драма", 105, "Полнометражный"),
        )
    }

}
