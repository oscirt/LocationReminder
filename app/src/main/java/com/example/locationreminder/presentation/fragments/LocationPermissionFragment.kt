package com.example.locationreminder.presentation.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentLocationPermissionBinding
import com.google.android.material.snackbar.Snackbar

class LocationPermissionFragment : Fragment() {

    private lateinit var binding: FragmentLocationPermissionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_location_permission,
            container,
            false
        )

        val permissionsLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (!it.containsValue(false)) {
                    findNavController().navigateUp()
                } else {
                    Snackbar.make(
                        requireView(),
                        "Без этого разрешения некоторые функции не будут доступны.",
                        Snackbar.LENGTH_LONG
                    ).show()
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
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
    }

}