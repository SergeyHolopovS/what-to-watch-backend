package com.what_to_watch.studio.domain.repository

import com.what_to_watch.studio.domain.model.Studio
import java.util.UUID

interface StudioRepository {

    fun addStudio(name: String): Studio

    fun getStudio(id: UUID): Studio?

    fun getAllStudios(): List<Studio>

}
