package com.what_to_watch.cartoon.application.usecase.getall

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import org.springframework.stereotype.Component

@Component
class GetAllCartoonsUseCase (
    val cartoonRepository: CartoonRepository
) {

    fun execute(): List<Cartoon> = cartoonRepository.getAllCartoons()

}
