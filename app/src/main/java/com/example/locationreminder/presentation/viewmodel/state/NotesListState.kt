package com.example.locationreminder.presentation.viewmodel.state

import com.example.domain.models.Note

data class NotesListState(
    val notesList: List<Note>
)