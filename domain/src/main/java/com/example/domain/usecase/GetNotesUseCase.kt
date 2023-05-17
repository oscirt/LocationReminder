package com.example.domain.usecase

import com.example.domain.models.Note
import com.example.domain.repository.NotesRepository

class GetNotesUseCase(
    private val notesRepository: NotesRepository
) {
    suspend fun execute() : List<Note> {
        return notesRepository.getAll()
    }
}