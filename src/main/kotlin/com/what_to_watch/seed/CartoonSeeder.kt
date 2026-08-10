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
        val added = FAKE_CARTOONS.count { (title, releaseYear) ->
            if (cartoonRepository.existsByTitle(title)) {
                false
            } else {
                cartoonRepository.addCartoon(title, releaseYear)
                true
            }
        }
        if (added > 0) {
            logger.info { "Добавлено фейковых мультфильмов: $added" }
        }
    }

    private companion object {
        val FAKE_CARTOONS = listOf(
            "Ну, погоди!" to 1969,
            "Простоквашино" to 1978,
            "Ёжик в тумане" to 1975,
            "Малыш и Карлсон" to 1968,
            "Ледниковый период" to 2002,
            "Шрек" to 2001,
            "История игрушек" to 1995,
            "В поисках Немо" to 2003,
            "Головоломка" to 2015,
            "Тайна Коко" to 2017,
        )
    }

}
