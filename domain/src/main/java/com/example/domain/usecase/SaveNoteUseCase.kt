package com.example.domain.usecase

import com.example.domain.models.Note
import com.example.domain.repository.NotesRepository

class SaveNoteUseCase(
    private val notesRepository: NotesRepository
) {
    suspend fun execute(note: Note) : Long {
        return notesRepository.addNote(note = note)
    }
}