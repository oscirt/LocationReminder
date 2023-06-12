package com.example.locationreminder.presentation.viewmodel.event

import com.google.android.gms.maps.model.LatLng

interface LocationChooseFragmentEvent

class ChangePointEvent(val name: String, val point: LatLng) : LocationChooseFragmentEvent