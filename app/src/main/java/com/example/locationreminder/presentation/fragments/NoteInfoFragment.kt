package com.example.locationreminder.presentation.fragments

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentNoteInfoBinding
import com.example.locationreminder.other.Constants.REACHED_NOTIFICATION_ID
import com.example.locationreminder.other.TrackingUtility
import com.example.locationreminder.presentation.viewmodel.NoteInfoViewModel
import com.example.locationreminder.presentation.viewmodel.event.GetNoteInfoById
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NoteInfoFragment : Fragment(), OnMapReadyCallback {

    private lateinit var binding: FragmentNoteInfoBinding
    private val viewModel by viewModels<NoteInfoViewModel>()

    private val args: NoteInfoFragmentArgs by navArgs()

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
            R.layout.fragment_note_info,
            container,
            false
        )

        val notificationManager = ContextCompat.getSystemService(
            requireContext(), NotificationManager::class.java) as NotificationManager
        notificationManager.cancel(REACHED_NOTIFICATION_ID)

        viewModel.send(GetNoteInfoById(args.id))

        viewModel.state.observe(viewLifecycleOwner) {
            binding.noteState = it
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
            }
        }
    }

    private fun addCircleAround(point: LatLng) {
        mMap.addCircle(
            CircleOptions().center(point).radius(200.0)
                .fillColor(requireContext().getColor(R.color.map_marker_background_color))
                .strokeWidth(10f)
                .strokeColor(requireContext().getColor(R.color.map_marker_stroke_color))
        )
    }

}