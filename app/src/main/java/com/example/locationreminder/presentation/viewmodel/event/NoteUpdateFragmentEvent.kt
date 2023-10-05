package com.example.locationreminder.presentation.viewmodel.event

import com.example.domain.models.Note
import com.google.android.gms.maps.model.LatLng

interface NoteUpdateFragmentEvent

class GetNoteById(val id: Long) : NoteUpdateFragmentEvent
class UpdateNote(val note: Note) : NoteUpdateFragmentEvent
class ChangePoint(val name: String, val point: LatLng) : NoteUpdateFragmentEvent