package com.what_to_watch.cartoon.infrastructure.persistance.repository

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.cartoon.infrastructure.mappers.CartoonMapper
import com.what_to_watch.cartoon.infrastructure.persistance.entity.CartoonJpaEntity
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class CartoonRepositoryImpl(
    private val cartoonJpaRepository: CartoonJpaRepository,
    private val cartoonMapper: CartoonMapper,
) : CartoonRepository {

    override fun addCartoon(title: String, releaseYear: Int): Cartoon {
        val entity = CartoonJpaEntity(
            title = title,
            releaseYear = releaseYear,
        )
        return cartoonMapper.toModel(cartoonJpaRepository.save(entity))
    }

    override fun saveCartoon(cartoon: Cartoon): Cartoon {
        val entity = cartoonJpaRepository.save(cartoonMapper.toEntity(cartoon))
        return cartoonMapper.toModel(entity)
    }

    override fun getCartoon(id: UUID): Cartoon? =
        cartoonJpaRepository.findById(id)
            .map(cartoonMapper::toModel)
            .orElse(null)

    override fun getAllCartoons(): List<Cartoon> =
        cartoonJpaRepository.findAll().map(cartoonMapper::toModel)

    override fun getRandomUnratedCartoon(): Cartoon? =
        cartoonJpaRepository.findRandomWithZeroRating()?.let(cartoonMapper::toModel)

    override fun deleteCartoon(id: UUID) {
        cartoonJpaRepository.deleteById(id)
    }

    override fun existsByTitle(title: String): Boolean =
        cartoonJpaRepository.existsByTitle(title)

    override fun searchByTitle(query: String): List<Cartoon> =
        cartoonJpaRepository.findByTitleContainingIgnoreCase(query).map(cartoonMapper::toModel)

}
