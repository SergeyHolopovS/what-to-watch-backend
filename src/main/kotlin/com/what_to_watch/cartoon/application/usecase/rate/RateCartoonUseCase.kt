package com.what_to_watch.cartoon.application.usecase.rate

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.exceptions.cartoon.CartoonNotFoundException
import org.springframework.stereotype.Component

@Component
class RateCartoonUseCase (
    val cartoonRepository: CartoonRepository
) {

    fun execute(command: RateCartoonCommand): Cartoon {
        val cartoon = cartoonRepository.getCartoon(command.id)
            ?: throw CartoonNotFoundException(command.id)
        return cartoonRepository.saveCartoon(cartoon.copy(rating = command.rating))
    }

}
