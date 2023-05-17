package com.example.domain.repository

import com.example.domain.models.Note

interface NotesRepository {
    suspend fun getAll() : List<Note>
    suspend fun getById(id: Long) : Note
    suspend fun addNote(note: Note) : Long
}