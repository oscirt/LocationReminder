package com.example.locationreminder.di

import android.content.Context
import com.example.data.repository.NotesRepositoryImpl
import com.example.data.storage.NotesStorage
import com.example.data.storage.room.RoomNotesStorage
import com.example.domain.repository.NotesRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    @Singleton
    fun provideNotesRepository(notesStorage: NotesStorage): NotesRepository {
        return NotesRepositoryImpl(notesStorage = notesStorage)
    }

    @Provides
    @Singleton
    fun provideNotesStorage(@ApplicationContext context: Context): NotesStorage {
        return RoomNotesStorage(context = context)
    }

    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

}