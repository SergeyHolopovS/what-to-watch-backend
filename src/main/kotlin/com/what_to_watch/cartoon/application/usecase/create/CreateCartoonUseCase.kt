package com.what_to_watch.cartoon.application.usecase.create

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.exceptions.cartoon.CartoonAlreadyExistsException
import org.springframework.stereotype.Component

@Component
class CreateCartoonUseCase (
    val cartoonRepository: CartoonRepository
) {

    fun execute(command: CreateCartoonCommand): Cartoon {
        if (cartoonRepository.existsByTitle(command.title)) {
            throw CartoonAlreadyExistsException(command.title)
        }
        return cartoonRepository.addCartoon(
            command.title, command.releaseYear, command.genre, command.duration, command.type,
        )
    }

}
