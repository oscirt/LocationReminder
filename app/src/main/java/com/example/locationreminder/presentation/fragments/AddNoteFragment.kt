package com.example.locationreminder.presentation.fragments

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.data.storage.location.LocationUtility
import com.example.domain.models.Note
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentAddNoteBinding
import com.example.locationreminder.presentation.viewmodel.AddNoteViewModel
import com.example.locationreminder.presentation.viewmodel.event.AddNoteEvent
import com.example.locationreminder.presentation.viewmodel.event.SaveInputsEvent
import com.example.locationreminder.presentation.viewmodel.state.locationState.ChosenLocation
import com.example.locationreminder.services.LocationService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AddNoteFragment : Fragment() {

    private lateinit var binding: FragmentAddNoteBinding
    private val viewModel by navGraphViewModels<AddNoteViewModel>(R.id.add_note_graph) { defaultViewModelProviderFactory }
    private val args: AddNoteFragmentArgs by navArgs()

    private var isBound = false
    private var locationService: LocationService? = null

    private var chosenLocation: ChosenLocation? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_note,
            container,
            false
        )

        val serviceIntent = Intent(requireContext(), LocationService::class.java)
        requireContext().bindService(serviceIntent, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                isBound = true
                locationService = (service as? LocationService.LocationServiceBinder)?.getService()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                isBound = false
                locationService = null
            }
        }, Context.BIND_AUTO_CREATE)

        binding.saveNoteFab.setOnClickListener {
            Log.d("TAG", "onCreateView: $chosenLocation")
            if (!checkValuesNotEmpty()) {
                val note = Note(
                    id = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
                    name = binding.nameEdit.editText!!.text.toString(),
                    description = binding.descriptionEdit.editText!!.text.toString(),
                    placeName = chosenLocation!!.name,
                    latitude = chosenLocation!!.point!!.latitude,
                    longitude = chosenLocation!!.point!!.longitude,
                    isChecked = false
                )
                viewModel.send(AddNoteEvent(note = note))
            }
        }

        val locationLauncher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                viewModel.send(
                    SaveInputsEvent(
                        name = binding.nameEdit.editText?.text.toString(),
                        description = binding.descriptionEdit.editText?.text.toString()
                    )
                )
                findNavController().navigate(R.id.action_addNoteFragment_to_fragmentLocationChoose)
            }
        }

        binding.locationPicker.setOnClickListener {
            if (LocationUtility.hasLocationPermission(requireContext())) {
                val locationRequest = LocationRequest.Builder(3000)
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .build()
                val locationSettingsRequest = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)
                    .build()

                LocationServices.getSettingsClient(requireContext())
                    .checkLocationSettings(locationSettingsRequest)
                    .addOnCompleteListener {
                        try {
                            it.getResult(ApiException::class.java)
                            viewModel.send(
                                SaveInputsEvent(
                                    name = binding.nameEdit.editText?.text.toString(),
                                    description = binding.descriptionEdit.editText?.text.toString()
                                )
                            )
                            findNavController().navigate(R.id.action_addNoteFragment_to_fragmentLocationChoose)
                            Log.d(TAG, "GPS already enabled")
                        } catch (e: ApiException) {
                            if (e.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                                val exception = e as ResolvableApiException
                                try {
                                    val intentSenderRequest = IntentSenderRequest
                                        .Builder(exception.resolution)
                                        .build()
                                    locationLauncher.launch(intentSenderRequest)
                                } catch (sendIntentException: IntentSender.SendIntentException) {
                                    sendIntentException.printStackTrace()
                                }
                            }
                            if (e.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                                Snackbar.make(
                                    requireView(),
                                    "GPS недоступно.",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            }
        }

        if (args.location != null) {
            chosenLocation = args.location
            binding.chosenLocation.text =
                if (chosenLocation?.name == "") requireContext().getString(R.string.map_point_chosen)
                else requireContext().getString(R.string.map_poi_chosen, chosenLocation?.name)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            if (it.id != -1L) {
                Log.d(TAG, "added")
                if (isBound) locationService!!.updateNotesList()
                findNavController().navigateUp()
            }
            Log.d("TAG", "onCreateView: ${it.name}|${it.description}")
            binding.nameEdit.editText?.setText(it.name)
            binding.descriptionEdit.editText?.setText(it.description)
        }

        return binding.root
    }

    private fun checkValuesNotEmpty(): Boolean {
        var error = false
        if (binding.nameEdit.editText!!.text.toString().isBlank()) {
            binding.nameEdit.error = "Необходимо указать название"
            error = true
        } else {
            binding.nameEdit.error = null
        }
        Log.d("TAG", "2onCreateView: $chosenLocation")
        if (chosenLocation == null) {
            Log.d("TAG", "checkValuesNotEmpty: ${args.location}")
            binding.chosenLocation.text = "Необходимо выбрать местоположение"
            error = true
        }
        return error
    }

    private companion object {
        private const val TAG = "AddNoteFragment"
    }

}