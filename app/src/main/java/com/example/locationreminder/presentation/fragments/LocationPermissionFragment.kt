package com.example.locationreminder.presentation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentLocationPermissionBinding
import com.example.locationreminder.other.LocationPermissionSettingsDialogFragment

class LocationPermissionFragment : Fragment() {

    private lateinit var binding: FragmentLocationPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            findNavController().navigate(R.id.action_locationPermissionFragment_to_notesListFragment)
        }
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_location_permission,
            container,
            false
        )

        val permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                Log.d(TAG, "onCreateView: $it")
                if (!it.containsValue(false)) {
                    findNavController().navigate(R.id.action_locationPermissionFragment_to_notesListFragment)
                } else {
                    val dialog = LocationPermissionSettingsDialogFragment()
                    dialog.show(childFragmentManager, LocationPermissionSettingsDialogFragment.TAG)
                }
            }

        binding.permissionButton.setOnClickListener {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                permissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                permissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                )
            }
        }

        return binding.root
    }

    private companion object {
        private const val TAG = "LocationPermissionFragment"
    }

}