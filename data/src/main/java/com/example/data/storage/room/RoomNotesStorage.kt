package com.example.data.storage.room

import android.content.Context
import com.example.data.storage.NotesStorage
import com.example.data.storage.room.database.NotesDatabase
import com.example.data.storage.room.models.NoteEntity
import kotlinx.coroutines.flow.Flow

class RoomNotesStorage(
    context: Context
) : NotesStorage {

    private val notesDao = NotesDatabase.getDatabase(context).notesDao()

    override fun getAll(): Flow<List<NoteEntity>> {
        return notesDao.getAll()
    }

    override suspend fun getById(id: Long): NoteEntity {
        return notesDao.getById(id)
    }

    override suspend fun addNote(note: NoteEntity): Long {
        return notesDao.addNote(note)
    }

    override suspend fun updateNote(note: NoteEntity) {
        notesDao.updateNote(note)
    }
}