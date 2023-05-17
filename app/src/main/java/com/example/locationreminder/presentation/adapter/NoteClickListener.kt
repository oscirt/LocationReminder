package com.example.locationreminder.presentation.adapter

import com.example.domain.models.Note

class NoteClickListener(private val clickListener: (Note) -> Unit) {
    fun onClick(note: Note) = clickListener(note)
}