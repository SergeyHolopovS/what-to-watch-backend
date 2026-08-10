package com.what_to_watch.seed

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.series.domain.repository.SeriesRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@SpringBootTest
@ActiveProfiles("test", "dev")
class SeedersTest {

    @Autowired
    private lateinit var cartoonRepository: CartoonRepository

    @Autowired
    private lateinit var seriesRepository: SeriesRepository

    @Autowired
    private lateinit var cartoonSeeder: CartoonSeeder

    @Autowired
    private lateinit var seriesSeeder: SeriesSeeder

    @Test
    fun `сидер добавляет фейковые данные и не дублирует их при повторном запуске`() {
        cartoonSeeder.run()
        seriesSeeder.run()

        val cartoonsAfterFirstRun = cartoonRepository.getAllCartoons().size
        val seriesAfterFirstRun = seriesRepository.getAllSeries().size

        assertTrue(cartoonsAfterFirstRun > 0, "фейковые мультфильмы должны были добавиться")
        assertTrue(seriesAfterFirstRun > 0, "фейковые сериалы должны были добавиться")

        cartoonSeeder.run()
        seriesSeeder.run()

        assertEquals(cartoonsAfterFirstRun, cartoonRepository.getAllCartoons().size)
        assertEquals(seriesAfterFirstRun, seriesRepository.getAllSeries().size)
    }

}
