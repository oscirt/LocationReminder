package com.example.locationreminder.presentation.viewmodel.state.locationState

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChosenLocation (
    val name: String,
    val point: LatLng?
) : Parcelable