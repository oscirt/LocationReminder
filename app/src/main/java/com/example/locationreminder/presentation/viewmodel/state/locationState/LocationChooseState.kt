package com.example.locationreminder.presentation.viewmodel.state.locationState

import com.google.android.gms.maps.model.LatLng

data class LocationChooseState (
    val name: String,
    val point: LatLng?
)