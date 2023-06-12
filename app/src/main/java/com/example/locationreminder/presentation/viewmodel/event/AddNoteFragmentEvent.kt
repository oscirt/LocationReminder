package com.example.locationreminder.presentation.viewmodel.event

import com.example.domain.models.Note

interface AddNoteFragmentEvent

class AddNoteEvent(val note: Note) : AddNoteFragmentEvent

class SaveInputsEvent(val name: String, val description: String) : AddNoteFragmentEvent