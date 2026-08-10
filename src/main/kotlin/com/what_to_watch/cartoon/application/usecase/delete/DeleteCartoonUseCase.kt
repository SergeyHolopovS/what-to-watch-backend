package com.what_to_watch.cartoon.application.usecase.delete

import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.exceptions.cartoon.CartoonNotFoundException
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DeleteCartoonUseCase (
    val cartoonRepository: CartoonRepository
) {

    fun execute(id: UUID) {
        cartoonRepository.getCartoon(id) ?: throw CartoonNotFoundException(id)
        cartoonRepository.deleteCartoon(id)
    }

}
