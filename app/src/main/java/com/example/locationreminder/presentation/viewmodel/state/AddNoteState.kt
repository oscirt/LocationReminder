package com.example.locationreminder.presentation.viewmodel.state

data class AddNoteState(
    val id: Long,
    val name: String,
    val description: String,
    val placeCoordinates: String
)