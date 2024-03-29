package com.example.locationreminder.presentation.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.data.storage.location.LocationUtility
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentNotesListBinding
import com.example.locationreminder.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.locationreminder.presentation.adapter.NoteClickListener
import com.example.locationreminder.presentation.adapter.NotesRecyclerViewAdapter
import com.example.locationreminder.presentation.viewmodel.NotesViewModel
import com.example.locationreminder.presentation.viewmodel.event.CheckerNoteListEvent
import com.example.locationreminder.services.LocationService
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NotesListFragment : Fragment() {

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var authUiInstance: AuthUI

    private lateinit var binding: FragmentNotesListBinding
    private val notesListViewModel by viewModels<NotesViewModel>()

    private lateinit var notesAdapter: NotesRecyclerViewAdapter
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().intent.extras?.let {
            Log.d(TAG, "EXTRAS")
            findNavController().navigate(
                NotesListFragmentDirections.actionNotesListFragmentToNoteInfoFragment(
                    it.getLong("id")
                )
            )
            return null
        } ?: run {
            binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_notes_list,
                container,
                false
            )

            signInLauncher = registerForActivityResult(
                FirebaseAuthUIActivityResultContract()
            ) {
                onSignInResult(it)
            }

            notesAdapter = NotesRecyclerViewAdapter(
                NoteClickListener {
                    findNavController().navigate(
                        NotesListFragmentDirections.actionNotesListFragmentToNoteUpdateFragment(it.id)
                    )
                },
                NoteClickListener {
                    notesListViewModel.send(CheckerNoteListEvent(it))
                }
            )

            binding.notesList.adapter = notesAdapter

            notesListViewModel.state.observe(viewLifecycleOwner) {
                notesAdapter.submitList(it.notesList)
                binding.emptyBlock.visibility =
                    if (it.notesList.isEmpty()) View.VISIBLE else View.GONE
            }

            binding.addNoteFab.setOnClickListener {
                findNavController().navigate(R.id.action_notesListFragment_to_add_note_graph)
            }

            val locationLauncher = registerForActivityResult(
                ActivityResultContracts.StartIntentSenderForResult()
            ) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
                }
            }

            binding.turnOnLocationServiceFab.setOnClickListener {
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
                                sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
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

            return binding.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (auth.currentUser == null) {
            signIn()
        }
    }

    private fun signIn() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val signInIntent = authUiInstance
            .createSignInIntentBuilder()
            .setLogo(R.drawable.folded_map)
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_LocationReminder)
            .build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "SignIn ok")
        } else {
            Log.d(TAG, "SignIn not ok: ${result.resultCode}")
            Snackbar.make(
                requireView(),
                "Error: ${result.idpResponse?.error?.message}",
                Snackbar.LENGTH_LONG
            )
                .show()
            signIn()
        }
    }

    private fun sendCommandToService(action: String) {
        val activity = requireActivity()
        Intent(activity.applicationContext, LocationService::class.java).apply {
            this.action = action
            activity.startService(this)
        }
    }

    private companion object {
        private const val TAG = "NotesListFragment"
    }
}