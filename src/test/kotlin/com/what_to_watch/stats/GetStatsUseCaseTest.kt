package com.what_to_watch.stats

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import com.what_to_watch.stats.application.usecase.getstats.GetStatsUseCase
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetStatsUseCaseTest {

    @Test
    fun `считает статистику по мультфильмам и сериалам вместе`() {
        val cartoons = listOf(cartoon(0), cartoon(3), cartoon(5), cartoon(8))
        val series = listOf(series(0), series(1), series(10))
        val useCase = GetStatsUseCase(FakeCartoonRepository(cartoons), FakeSeriesRepository(series))

        val result = useCase.execute()

        // рейтинги 1..5 среди обоих каталогов: 3, 5 (мультфильмы) и 1 (сериал)
        assertEquals(3L, result.ratedFromOneToFive)
        assertEquals(4L, result.totalCartoons)
        assertEquals(3L, result.totalSeries)
        // оценённые (без нулей): 3, 5, 8, 1, 10 -> сумма 27, count 5 -> среднее 5.4
        assertEquals(5.4, result.averageRating!!, 0.0001)
    }

    @Test
    fun `без оценённых элементов средняя оценка null, а счётчик 1-5 нулевой`() {
        val useCase = GetStatsUseCase(
            FakeCartoonRepository(listOf(cartoon(0), cartoon(0))),
            FakeSeriesRepository(listOf(series(0))),
        )

        val result = useCase.execute()

        assertNull(result.averageRating)
        assertEquals(0L, result.ratedFromOneToFive)
        assertEquals(2L, result.totalCartoons)
        assertEquals(1L, result.totalSeries)
    }

    @Test
    fun `рейтинги вне диапазона 1-5 не попадают в счётчик, но входят в среднюю`() {
        val useCase = GetStatsUseCase(
            FakeCartoonRepository(listOf(cartoon(6), cartoon(10))),
            FakeSeriesRepository(emptyList()),
        )

        val result = useCase.execute()

        assertEquals(0L, result.ratedFromOneToFive)
        assertEquals(8.0, result.averageRating!!, 0.0001)
    }

    private fun cartoon(rating: Int) = Cartoon(
        UUID.randomUUID(), "cartoon-$rating-${UUID.randomUUID()}", 2000, rating, "Драма", 90, "Полнометражный",
        UUID.randomUUID(), Instant.now(),
    )

    private fun series(rating: Int) = Series(
        UUID.randomUUID(), "series-$rating-${UUID.randomUUID()}", 2000, rating, "Драма", 45, "Сериал",
        UUID.randomUUID(), Instant.now(),
    )

    private class FakeCartoonRepository(private val cartoons: List<Cartoon>) : CartoonRepository {
        override fun addCartoon(
            title: String, releaseYear: Int, genre: String, duration: Int, type: String, studioId: UUID,
        ) = throw UnsupportedOperationException()
        override fun saveCartoon(cartoon: Cartoon) = throw UnsupportedOperationException()
        override fun getCartoon(id: UUID) = throw UnsupportedOperationException()
        override fun getAllCartoons(): List<Cartoon> = cartoons
        override fun getRandomUnratedCartoon() = throw UnsupportedOperationException()
        override fun deleteCartoon(id: UUID) = throw UnsupportedOperationException()
        override fun existsByTitle(title: String) = throw UnsupportedOperationException()
        override fun search(title: String?, studioId: UUID?) = throw UnsupportedOperationException()
    }

    private class FakeSeriesRepository(private val series: List<Series>) : SeriesRepository {
        override fun addSeries(
            title: String, releaseYear: Int, genre: String, duration: Int, type: String, studioId: UUID,
        ) = throw UnsupportedOperationException()
        override fun saveSeries(series: Series) = throw UnsupportedOperationException()
        override fun getSeries(id: UUID) = throw UnsupportedOperationException()
        override fun getAllSeries(): List<Series> = series
        override fun getRandomUnratedSeries() = throw UnsupportedOperationException()
        override fun deleteSeries(id: UUID) = throw UnsupportedOperationException()
        override fun existsByTitle(title: String) = throw UnsupportedOperationException()
        override fun search(title: String?, studioId: UUID?) = throw UnsupportedOperationException()
    }

}
