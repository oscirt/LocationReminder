package com.example.locationreminder.presentation.viewmodel.event

import com.example.domain.models.Note

interface NotesListFragmentEvent

class GetNotesListEvent : NotesListFragmentEvent
class CheckerNoteListEvent(val note: Note) : NotesListFragmentEvent