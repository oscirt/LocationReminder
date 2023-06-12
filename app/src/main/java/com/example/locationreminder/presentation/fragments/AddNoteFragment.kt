package com.example.locationreminder.presentation.fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.example.domain.models.Note
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentAddNoteBinding
import com.example.locationreminder.presentation.viewmodel.AddNoteViewModel
import com.example.locationreminder.presentation.viewmodel.event.AddNoteEvent
import com.example.locationreminder.presentation.viewmodel.event.SaveInputsEvent
import com.example.locationreminder.presentation.viewmodel.state.locationState.ChosenLocation
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID

@AndroidEntryPoint
class AddNoteFragment : Fragment() {

    private lateinit var binding: FragmentAddNoteBinding
    private val viewModel by navGraphViewModels<AddNoteViewModel>(R.id.add_note_graph) { defaultViewModelProviderFactory }
    private val args: AddNoteFragmentArgs by navArgs()

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

        binding.saveNoteFab.setOnClickListener {
            Log.d("TAG", "3onCreateView: $chosenLocation")
            if (!checkValuesNotEmpty()) {
                val note = Note(
                    id = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
                    name = binding.nameEdit.editText!!.text.toString(),
                    description = binding.descriptionEdit.editText!!.text.toString(),
                    placeName = chosenLocation!!.name,
                    latitude = chosenLocation!!.point!!.latitude,
                    longitude = chosenLocation!!.point!!.longitude
                )
                viewModel.send(AddNoteEvent(note = note))
            }
        }

        binding.locationReminder.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                viewModel.send(
                    SaveInputsEvent(
                        name = binding.nameEdit.editText?.text.toString(),
                        description = binding.descriptionEdit.editText?.text.toString()
                    )
                )
                findNavController().navigate(R.id.action_addNoteFragment_to_fragmentLocationChoose)
            }
        }

        if (args.location != null) {
            chosenLocation = args.location
            binding.chosenLocation.text =
                if (chosenLocation?.name == "") requireContext().getString(R.string.map_point_chosen)
                else requireContext().getString(R.string.map_poi_chosen, chosenLocation?.name)
        }

        viewModel.state.observe(viewLifecycleOwner) {
            if (it.id != -1L) findNavController().navigateUp()
            Log.d("TAG", "onCreateView: ${it.name}|${it.description}")
            binding.nameEdit.editText?.setText(it.name)
            binding.descriptionEdit.editText?.setText(it.description)
        }

        return binding.root
    }

    private fun checkValuesNotEmpty(): Boolean {
        var error = false
        if (binding.nameEdit.editText!!.text.toString().isBlank()) {
            binding.nameEdit.error = "Необходимо указать"
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

}