package com.what_to_watch.studio.application.usecase.create

import com.what_to_watch.studio.domain.model.Studio
import com.what_to_watch.studio.domain.repository.StudioRepository
import org.springframework.stereotype.Component

@Component
class CreateStudioUseCase (
    val studioRepository: StudioRepository
) {

    fun execute(command: CreateStudioCommand): Studio =
        studioRepository.addStudio(command.name)

}
