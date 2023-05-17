package com.example.locationreminder.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Note
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentAddNoteBinding
import com.example.locationreminder.presentation.viewmodel.AddNoteViewModel
import com.example.locationreminder.presentation.viewmodel.event.AddNoteEvent
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

@AndroidEntryPoint
class AddNoteFragment : Fragment() {

    private lateinit var binding: FragmentAddNoteBinding
    private val viewModel by viewModels<AddNoteViewModel>()

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
            val note = Note(
                id = UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE,
                name = binding.nameEdit.editText!!.text.toString(),
                description = binding.descriptionEdit.editText!!.text.toString(),
                placeCoordinates = ""
            )
            viewModel.send(AddNoteEvent(note = note))
        }

        viewModel.state.observe(viewLifecycleOwner) {
            if (it.id != -1L) findNavController().navigateUp()
        }

        return binding.root
    }

}