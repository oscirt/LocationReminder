package com.example.data.storage.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.data.storage.room.models.NoteEntity

@Dao
interface NotesDao {

    @Query("SELECT * FROM notes")
    suspend fun getAll() : List<NoteEntity>

    @Query("SELECT * FROM notes WHERE id == :id")
    suspend fun getById(id: Long) : NoteEntity

    @Insert
    suspend fun addNote(note: NoteEntity) : Long

    @Update
    suspend fun updateNote(note: NoteEntity)
}