package com.what_to_watch.studio.application.usecase.resolve

import com.what_to_watch.exceptions.studio.StudioNotFoundException
import com.what_to_watch.exceptions.studio.StudioRequiredException
import com.what_to_watch.studio.application.usecase.create.CreateStudioCommand
import com.what_to_watch.studio.application.usecase.create.CreateStudioUseCase
import com.what_to_watch.studio.domain.repository.StudioRepository
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * Общая логика для создания мультфильмов/сериалов: если передан studioId — используем
 * существующую студию, иначе создаём новую по studioName. Если не передано ни то ни другое — 400.
 */
@Component
class ResolveStudioUseCase(
    private val studioRepository: StudioRepository,
    private val createStudioUseCase: CreateStudioUseCase,
) {

    fun execute(studioId: UUID?, studioName: String?): UUID {
        if (studioId != null) {
            val studio = studioRepository.getStudio(studioId) ?: throw StudioNotFoundException(studioId)
            return studio.id
        }
        if (!studioName.isNullOrBlank()) {
            return createStudioUseCase.execute(CreateStudioCommand(studioName)).id
        }
        throw StudioRequiredException()
    }

}
