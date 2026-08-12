package com.what_to_watch.cartoon.application.usecase.create

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import com.what_to_watch.exceptions.cartoon.CartoonAlreadyExistsException
import com.what_to_watch.studio.application.usecase.resolve.ResolveStudioUseCase
import org.springframework.stereotype.Component

@Component
class CreateCartoonUseCase (
    val cartoonRepository: CartoonRepository,
    val resolveStudioUseCase: ResolveStudioUseCase,
) {

    fun execute(command: CreateCartoonCommand): Cartoon {
        if (cartoonRepository.existsByTitle(command.title)) {
            throw CartoonAlreadyExistsException(command.title)
        }
        val studioId = resolveStudioUseCase.execute(command.studioId, command.studioName)
        return cartoonRepository.addCartoon(
            command.title, command.releaseYear, command.genre, command.duration, command.type, studioId,
        )
    }

}
