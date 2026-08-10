package com.what_to_watch.cartoon.application.usecase.search

import com.what_to_watch.cartoon.domain.model.Cartoon
import com.what_to_watch.cartoon.domain.repository.CartoonRepository
import org.springframework.stereotype.Component

@Component
class SearchCartoonsUseCase (
    val cartoonRepository: CartoonRepository
) {

    fun execute(query: String): List<Cartoon> = cartoonRepository.searchByTitle(query)

}
