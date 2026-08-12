package com.what_to_watch.series.domain.repository

import com.what_to_watch.series.domain.model.Series
import java.util.UUID

interface SeriesRepository {

    fun addSeries(
        title: String,
        releaseYear: Int,
        genre: String,
        duration: Int,
        type: String,
        studioId: UUID,
    ): Series

    fun saveSeries(series: Series): Series

    fun getSeries(id: UUID): Series?

    fun getAllSeries(): List<Series>

    fun getRandomUnratedSeries(): Series?

    fun deleteSeries(id: UUID)

    fun existsByTitle(title: String): Boolean

    fun searchByTitle(query: String): List<Series>

}
