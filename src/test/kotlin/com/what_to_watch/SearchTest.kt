package com.what_to_watch

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.series.domain.repository.SeriesRepository
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

    @Test
    fun `поиск мультфильмов по подстроке без учёта регистра`() {
        cartoonRepository.addCartoon("Ну, погоди!", 1969)
        cartoonRepository.addCartoon("Простоквашино", 1978)

        mockMvc.get("/cartoons/search") {
            param("title", "погод")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].title") { value("Ну, погоди!") }
        }

        mockMvc.get("/cartoons/search") {
            param("title", "ПОГОД")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
        }
    }

    @Test
    fun `поиск сериалов по подстроке без учёта регистра`() {
        seriesRepository.addSeries("Во все тяжкие", 2008)
        seriesRepository.addSeries("Друзья", 1994)

        mockMvc.get("/series/search") {
            param("title", "тяжк")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(1) }
            jsonPath("$[0].title") { value("Во все тяжкие") }
        }
    }

    @Test
    fun `пустой запрос отклоняется`() {
        mockMvc.get("/cartoons/search") {
            param("title", "")
        }.andExpect { status { isBadRequest() } }
    }

    @Test
    fun `отсутствие ни одного совпадения возвращает пустой список`() {
        mockMvc.get("/cartoons/search") {
            param("title", "несуществующий-мультфильм-xyz")
        }.andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(0) }
        }
    }

}
