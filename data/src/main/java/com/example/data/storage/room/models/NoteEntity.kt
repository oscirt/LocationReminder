package com.example.data.storage.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "place_name") val placeName: String,
    @ColumnInfo(name = "place_latitude") val latitude: Double,
    @ColumnInfo(name = "place_longitude") val longitude: Double
)