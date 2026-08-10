package com.what_to_watch.series.infrastructure.persistance.repository

import com.what_to_watch.series.domain.model.Series
import com.what_to_watch.series.domain.repository.SeriesRepository
import com.what_to_watch.series.infrastructure.mappers.SeriesMapper
import com.what_to_watch.series.infrastructure.persistance.entity.SeriesJpaEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class SeriesRepositoryImpl(
    private val seriesJpaRepository: SeriesJpaRepository,
    private val seriesMapper: SeriesMapper,
) : SeriesRepository {

    override fun addSeries(title: String, releaseYear: Int): Series {
        val entity = SeriesJpaEntity(
            title = title,
            releaseYear = releaseYear,
        )
        return seriesMapper.toModel(seriesJpaRepository.save(entity))
    }

    override fun saveSeries(series: Series): Series {
        val entity = seriesJpaRepository.save(seriesMapper.toEntity(series))
        return seriesMapper.toModel(entity)
    }

    override fun getSeries(id: UUID): Series? =
        seriesJpaRepository.findById(id)
            .map(seriesMapper::toModel)
            .orElse(null)

    override fun getAllSeries(): List<Series> =
        seriesJpaRepository.findAll().map(seriesMapper::toModel)

    override fun getRandomUnratedSeries(): Series? =
        seriesJpaRepository.findRandomWithZeroRating()?.let(seriesMapper::toModel)

    override fun deleteSeries(id: UUID) {
        seriesJpaRepository.deleteById(id)
    }

    override fun existsByTitle(title: String): Boolean =
        seriesJpaRepository.existsByTitle(title)

    override fun searchByTitle(query: String): List<Series> =
        seriesJpaRepository.findByTitleContainingIgnoreCase(query).map(seriesMapper::toModel)

}
