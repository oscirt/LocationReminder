package com.example.domain.usecase

import com.example.domain.models.Note
import com.example.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(
    private val notesRepository: NotesRepository
) {
    fun execute() : Flow<List<Note>> {
        return notesRepository.getAll()
    }
}