package com.example.data.repository

import com.example.data.storage.NotesStorage
import com.example.data.storage.room.models.NoteEntity
import com.example.domain.models.Note
import com.example.domain.repository.NotesRepository

class NotesRepositoryImpl(
    private val notesStorage: NotesStorage
) : NotesRepository {
    override suspend fun getAll(): List<Note> {
        return notesStorage.getAll().map { mapNoteEntityToNote(it) }
    }

    override suspend fun getById(id: Long): Note {
        val noteEntity = notesStorage.getById(id)
        return mapNoteEntityToNote(noteEntity)
    }

    override suspend fun addNote(note: Note): Long {
        val noteEntity = mapNoteToNoteEntity(note)
        return notesStorage.addNote(noteEntity)
    }

    override suspend fun updateNote(note: Note) {
        val noteEntity = mapNoteToNoteEntity(note)
        notesStorage.updateNote(noteEntity)
    }

    private fun mapNoteEntityToNote(noteEntity: NoteEntity) : Note {
        return Note(
            id = noteEntity.id,
            name = noteEntity.name,
            description = noteEntity.description,
            placeName = noteEntity.placeName,
            latitude = noteEntity.latitude,
            longitude = noteEntity.longitude,
            isChecked = noteEntity.isChecked
        )
    }

    private fun mapNoteToNoteEntity(note: Note) : NoteEntity {
        return NoteEntity(
            id = note.id,
            name = note.name,
            description = note.description,
            placeName = note.placeName,
            latitude = note.latitude,
            longitude = note.longitude,
            isChecked = note.isChecked
        )
    }
}