package com.what_to_watch.stats

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.series.domain.repository.SeriesRepository
import com.what_to_watch.studio.domain.repository.StudioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import tools.jackson.databind.ObjectMapper
import kotlin.test.Test
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StatsControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var cartoonRepository: CartoonRepository

    @Autowired
    private lateinit var seriesRepository: SeriesRepository

    @Autowired
    private lateinit var studioRepository: StudioRepository

    /**
     * Контекст и БД общие с другими тестовыми классами, поэтому сверяем не абсолютные
     * значения, а изменение счётчиков после добавления заведомо известных записей.
     */
    @Test
    fun `доступен без авторизации и отражает добавленные записи`() {
        val before = fetchStats()

        val studioId = studioRepository.addStudio("Статистика студия ${System.nanoTime()}").id
        cartoonRepository.addCartoon(
            "Статистика мультфильм ${System.nanoTime()}", 2000, "Драма", 90, "Полнометражный", studioId,
        )
        seriesRepository.addSeries("Статистика сериал ${System.nanoTime()}", 2000, "Драма", 45, "Сериал", studioId)

        val after = fetchStats()

        assertEquals(before.get("totalCartoons").asLong() + 1, after.get("totalCartoons").asLong())
        assertEquals(before.get("totalSeries").asLong() + 1, after.get("totalSeries").asLong())
    }

    private fun fetchStats() =
        objectMapper.readTree(
            mockMvc.get("/stats")
                .andExpect { status { isOk() } }
                .andReturn()
                .response
                .contentAsString
        )

}
