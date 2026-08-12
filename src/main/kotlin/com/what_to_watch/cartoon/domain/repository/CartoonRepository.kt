package com.what_to_watch.cartoon.domain.repository

import com.what_to_watch.cartoon.domain.model.Cartoon
import java.util.UUID

interface CartoonRepository {

    fun addCartoon(
        title: String,
        releaseYear: Int,
        genre: String,
        duration: Int,
        type: String,
        studioId: UUID,
    ): Cartoon

    fun saveCartoon(cartoon: Cartoon): Cartoon

    fun getCartoon(id: UUID): Cartoon?

    fun getAllCartoons(): List<Cartoon>

    fun getRandomUnratedCartoon(): Cartoon?

    fun deleteCartoon(id: UUID)

    fun existsByTitle(title: String): Boolean

    fun searchByTitle(query: String): List<Cartoon>

}
