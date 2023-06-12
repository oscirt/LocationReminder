package com.example.locationreminder.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.locationreminder.presentation.viewmodel.event.ChangePointEvent
import com.example.locationreminder.presentation.viewmodel.event.LocationChooseFragmentEvent
import com.example.locationreminder.presentation.viewmodel.state.locationState.LocationChooseState
import com.google.android.gms.maps.model.LatLng

class LocationChooseViewModel : ViewModel() {

    private var _state = MutableLiveData<LocationChooseState>()
    val state: LiveData<LocationChooseState> get() = _state

    init {
        _state.value = LocationChooseState(
            name = "",
            point = null
        )
    }

    fun send(event: LocationChooseFragmentEvent) {
        when (event) {
            is ChangePointEvent -> {
                changePoint(event.name, event.point)
            }
        }
    }

    private fun changePoint(name: String, point: LatLng) {
        _state.value = LocationChooseState(name = name, point = point)
    }
}