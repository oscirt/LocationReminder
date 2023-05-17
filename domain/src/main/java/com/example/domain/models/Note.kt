package com.example.domain.models

data class Note (
    val id: Long,
    val name: String,
    val description: String,
    val placeCoordinates: String // TODO: поменять на норм тип
)