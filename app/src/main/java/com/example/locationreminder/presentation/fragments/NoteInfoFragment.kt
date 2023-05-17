package com.example.locationreminder.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.domain.models.Note
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentNoteInfoBinding
import com.example.locationreminder.presentation.viewmodel.NoteInfoViewModel
import com.example.locationreminder.presentation.viewmodel.event.GetNoteById
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteInfoFragment : Fragment() {

    private lateinit var binding: FragmentNoteInfoBinding
    private val viewModel by viewModels<NoteInfoViewModel>()

    private val args: NoteInfoFragmentArgs by navArgs()

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

        viewModel.send(GetNoteById(args.id))

        viewModel.state.observe(viewLifecycleOwner) {
            binding.note = Note(
                id = it.id,
                name = it.name,
                description = it.description,
                placeCoordinates = it.placeCoordinates
            )
        }

        return binding.root
    }

}