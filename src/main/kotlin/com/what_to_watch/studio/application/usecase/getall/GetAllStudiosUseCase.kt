package com.what_to_watch.studio.application.usecase.getall

import com.what_to_watch.studio.domain.model.Studio
import com.what_to_watch.studio.domain.repository.StudioRepository
import org.springframework.stereotype.Component

@Component
class GetAllStudiosUseCase (
    val studioRepository: StudioRepository
) {

    fun execute(): List<Studio> = studioRepository.getAllStudios()

}
