package com.example.data.storage

import com.example.data.storage.room.models.NoteEntity

interface NotesStorage {
    suspend fun getAll() : List<NoteEntity>
    suspend fun getById(id: Long) : NoteEntity
    suspend fun addNote(note: NoteEntity) : Long
    suspend fun updateNote(note: NoteEntity)
}