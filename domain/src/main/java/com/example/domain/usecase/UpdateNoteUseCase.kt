package com.example.domain.usecase

import com.example.domain.models.Note
import com.example.domain.repository.NotesRepository

class UpdateNoteUseCase(
    private val notesRepository: NotesRepository
) {
    suspend fun execute(note: Note) {
        notesRepository.updateNote(note = note)
    }
}