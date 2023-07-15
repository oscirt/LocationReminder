package com.example.domain.repository

import com.example.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {
    fun getAll() : Flow<List<Note>>
    suspend fun getById(id: Long) : Note
    suspend fun addNote(note: Note) : Long
    suspend fun updateNote(note: Note)
}