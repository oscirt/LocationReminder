package com.example.locationreminder.presentation.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.domain.models.Note
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentNoteUpdateBinding
import com.example.locationreminder.other.TrackingUtility
import com.example.locationreminder.presentation.viewmodel.NoteUpdateViewModel
import com.example.locationreminder.presentation.viewmodel.event.ChangePoint
import com.example.locationreminder.presentation.viewmodel.event.GetNoteById
import com.example.locationreminder.presentation.viewmodel.event.UpdateNote
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnPoiClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteUpdateFragment : Fragment(), OnMapReadyCallback,
    OnPoiClickListener, OnMapClickListener {

    private lateinit var binding: FragmentNoteUpdateBinding
    private val viewModel by viewModels<NoteUpdateViewModel>()
    private val args: NoteUpdateFragmentArgs by navArgs()

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
            R.layout.fragment_note_update,
            container,
            false
        )

        viewModel.send(GetNoteById(args.id))

        viewModel.state.observe(viewLifecycleOwner) {
            binding.nameEdit.editText?.setText(it.name)
            binding.descriptionEdit.editText?.setText(it.description)
            binding.chosenLocation.text = it.placeName
        }

        binding.saveNoteFab.setOnClickListener {
            if (!checkValuesNotEmpty()) {
                val viewModelNote = viewModel.state.value!!
                val note = Note(
                    id = viewModelNote.id,
                    name = binding.nameEdit.editText!!.text.toString(),
                    description = binding.descriptionEdit.editText!!.text.toString(),
                    placeName = viewModelNote.placeName,
                    latitude = viewModelNote.latitude,
                    longitude = viewModelNote.longitude,
                    isChecked = viewModelNote.isChecked
                )
                viewModel.send(UpdateNote(note = note))
                findNavController().navigateUp()
            }
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return binding.root
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
                val note = viewModel.state.value
                val point = LatLng(note?.latitude ?: it.latitude, note?.longitude ?: it.longitude)
                mMap.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            point,
                            15f
                        )
                    )
                )
                mMap.addMarker(MarkerOptions().position(point))
                addCircleAround(point = point)
                mMap.uiSettings.isZoomControlsEnabled = true
                mMap.isMyLocationEnabled = true
                mMap.setOnMapClickListener(this)
                mMap.setOnPoiClickListener(this)
            }
        }
    }

    override fun onPoiClick(poi: PointOfInterest) {
        mMap.clear()
        viewModel.send(ChangePoint(name = poi.name, point = poi.latLng))
        mMap.addMarker(MarkerOptions().position(poi.latLng).title(poi.name))
        addCircleAround(point = poi.latLng)
    }

    override fun onMapClick(point: LatLng) {
        mMap.clear()
        viewModel.send(ChangePoint(name = "", point = point))
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

    private fun checkValuesNotEmpty(): Boolean {
        var error = false
        if (binding.nameEdit.editText!!.text.toString().isBlank()) {
            binding.nameEdit.error = "Необходимо указать название"
            error = true
        } else {
            binding.nameEdit.error = null
        }
        if (binding.chosenLocation.text == null) {
            binding.chosenLocation.text = "Необходимо выбрать местоположение"
            error = true
        }
        return error
    }
}