package com.example.data.storage.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import com.example.data.storage.room.dao.NotesDao
import com.example.data.storage.room.models.NoteEntity

@Database(entities = [NoteEntity::class], version = 2)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao() : NotesDao

    companion object {
        @Volatile
        private var INSTANCE: NotesDatabase? = null

        fun getDatabase(context: Context): NotesDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    NotesDatabase::class.java,
                    "notes_database"
                ).addMigrations(Migration(1, 2) {
                    it.execSQL("ALTER TABLE notes ADD is_checked INTEGER NOT NULL DEFAULT 0")
                })
                    .build()

                INSTANCE = instance

                instance
            }
        }
    }
}