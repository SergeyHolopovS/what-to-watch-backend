package com.what_to_watch

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.series.domain.repository.SeriesRepository
import com.what_to_watch.studio.domain.repository.StudioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import kotlin.test.Test

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SearchTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var cartoonRepository: CartoonRepository

    @Autowired
    private lateinit var seriesRepository: SeriesRepository

    @Autowired
    private lateinit var studioRepository: StudioRepository

    @Test
    fun `поиск мультфильмов по подстроке без учёта регистра через каталог`() {
        val studioId = studioRepository.addStudio("Тестовая студия ${System.nanoTime()}").id
        val title = "Ну, погоди! ${System.nanoTime()}"
        cartoonRepository.addCartoon(title, 1969, "Комедия", 10, "Короткометражный", studioId)

        mockMvc.get("/catalog") {
            param("query", title.uppercase())
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].title") { value(title) }
        }
    }

    @Test
    fun `поиск сериалов по подстроке без учёта регистра`() {
        val studioId = studioRepository.addStudio("Тестовая студия ${System.nanoTime()}").id
        seriesRepository.addSeries("Во все тяжкие", 2008, "Драма", 47, "Сериал", studioId)
        seriesRepository.addSeries("Друзья", 1994, "Комедия", 22, "Сериал", studioId)

        mockMvc.get("/series/search") {
            param("title", "тяжк")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].title") { value("Во все тяжкие") }
        }
    }

    @Test
    fun `поиск сериалов дополнительно фильтруется по studioId`() {
        val matchingStudioId = studioRepository.addStudio("Студия A ${System.nanoTime()}").id
        val otherStudioId = studioRepository.addStudio("Студия B ${System.nanoTime()}").id
        val title = "Общий сериал ${System.nanoTime()}"
        seriesRepository.addSeries(title, 2020, "Драма", 40, "Сериал", matchingStudioId)
        seriesRepository.addSeries(title + " второй", 2020, "Драма", 40, "Сериал", otherStudioId)

        mockMvc.get("/series/search") {
            param("title", title)
            param("studioId", matchingStudioId.toString())
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].studioId") { value(matchingStudioId.toString()) }
        }
    }

}
