package com.example.domain.usecase

import com.example.domain.models.Note
import com.example.domain.repository.NotesRepository

class GetNoteByIdUseCase(
    private val notesRepository: NotesRepository
) {
    suspend fun execute(id: Long) : Note {
        return notesRepository.getById(id = id)
    }
}