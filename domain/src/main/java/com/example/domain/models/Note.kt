package com.example.domain.models

data class Note (
    val id: Long,
    val name: String,
    val description: String,
    val placeName: String,
    val latitude: Double,
    val longitude: Double,
    var isChecked: Boolean
)