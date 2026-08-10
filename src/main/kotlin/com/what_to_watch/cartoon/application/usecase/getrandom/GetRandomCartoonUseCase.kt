package com.what_to_watch.cartoon.application.usecase.getrandom

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.exceptions.cartoon.NoCartoonsToWatchException
import org.springframework.stereotype.Component

@Component
class GetRandomCartoonUseCase (
    val cartoonRepository: CartoonRepository
) {

    fun execute(): Cartoon =
        cartoonRepository.getRandomUnratedCartoon()
            ?: throw NoCartoonsToWatchException()

}
