package com.example.locationreminder.presentation.viewmodel.state

data class AddNoteState(
    val id: Long,
    val name: String,
    val description: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double
)