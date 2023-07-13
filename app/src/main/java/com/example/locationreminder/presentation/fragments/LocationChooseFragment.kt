package com.example.locationreminder.presentation.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.data.storage.location.LocationUtility
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentLocationChooseBinding
import com.example.locationreminder.other.TrackingUtility
import com.example.locationreminder.presentation.viewmodel.LocationChooseViewModel
import com.example.locationreminder.presentation.viewmodel.event.ChangePointEvent
import com.example.locationreminder.presentation.viewmodel.state.locationState.ChosenLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.GoogleMap.OnPoiClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LocationChooseFragment : Fragment(), OnMapReadyCallback, OnMyLocationClickListener,
    OnMyLocationButtonClickListener, OnPoiClickListener, OnMapClickListener {

    private lateinit var binding: FragmentLocationChooseBinding
    private val viewModel by viewModels<LocationChooseViewModel>()

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_location_choose,
            container,
            false
        )

        binding.button.setOnClickListener {
            val state = viewModel.state.value!!
            findNavController().navigate(
                LocationChooseFragmentDirections
                    .actionFragmentLocationChooseToAddNoteFragment(
                        location = ChosenLocation(
                            name = state.name,
                            point = state.point
                        )
                    )
            )
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner) {
            if (it.point != null) {
                mMap.addMarker(MarkerOptions().position(it.point))
                addCircleAround(it.point)
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            val cancellationToken = CancellationTokenSource()
            fusedLocationProviderClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            ).addOnSuccessListener {
                mMap.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            15f
                        )
                    )
                )
                mMap.uiSettings.isZoomControlsEnabled = true
                mMap.isMyLocationEnabled = true
                mMap.setOnMapClickListener(this)
                mMap.setOnPoiClickListener(this)
            }
        }
    }

    override fun onMyLocationClick(location: Location) {
        Snackbar.make(requireView(), "Current location:\n" + location, Snackbar.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Snackbar.make(requireView(), "MyLocation button clicked", Snackbar.LENGTH_SHORT)
            .show()
        return false

    }

    override fun onPoiClick(poi: PointOfInterest) {
        mMap.clear()
        viewModel.send(ChangePointEvent(name = poi.name, point = poi.latLng))
        mMap.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
        addCircleAround(point = poi.latLng)
    }

    override fun onMapClick(point: LatLng) {
        mMap.clear()
        viewModel.send(ChangePointEvent(name = "", point = point))
        mMap.addMarker(MarkerOptions().position(point))
        addCircleAround(point = point)
    }

    private fun addCircleAround(point: LatLng) {
        mMap.addCircle(
            CircleOptions().center(point).radius(200.0)
                .fillColor(requireContext().getColor(R.color.map_marker_background_color))
                .strokeWidth(10f)
                .strokeColor(requireContext().getColor(R.color.map_marker_stroke_color))
        )
    }

    private companion object {
        private const val TAG = "LocationChooseFragment"
    }
}