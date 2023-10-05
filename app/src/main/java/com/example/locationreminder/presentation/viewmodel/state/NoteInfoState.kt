package com.example.locationreminder.presentation.viewmodel.state

data class NoteInfoState(
    val id: Long,
    val name: String,
    val description: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double
)