package com.example.locationreminder.di

import com.example.domain.repository.NotesRepository
import com.example.domain.usecase.GetNoteByIdUseCase
import com.example.domain.usecase.GetNotesUseCase
import com.example.domain.usecase.SaveNoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

    @Provides
    fun provideGetNotesUseCase(repository: NotesRepository) : GetNotesUseCase {
        return GetNotesUseCase(notesRepository = repository)
    }

    @Provides
    fun provideSaveNoteUseCase(repository: NotesRepository) : SaveNoteUseCase {
        return SaveNoteUseCase(notesRepository = repository)
    }

    @Provides
    fun provideGetNoteByIdUseCase(repository: NotesRepository) : GetNoteByIdUseCase {
        return GetNoteByIdUseCase(notesRepository = repository)
    }
}