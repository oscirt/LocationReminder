package com.example.locationreminder.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.locationreminder.R
import com.example.locationreminder.databinding.FragmentNotesListBinding
import com.example.locationreminder.presentation.adapter.NoteClickListener
import com.example.locationreminder.presentation.adapter.NotesRecyclerViewAdapter
import com.example.locationreminder.presentation.viewmodel.NotesViewModel
import com.example.locationreminder.presentation.viewmodel.event.GetNotesListEvent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesListFragment : Fragment() {

    private lateinit var binding: FragmentNotesListBinding
    private val notesListViewModel by viewModels<NotesViewModel>()

    private lateinit var notesAdapter: NotesRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_notes_list,
            container,
            false
        )

        notesAdapter = NotesRecyclerViewAdapter(NoteClickListener {
            findNavController().navigate(
                NotesListFragmentDirections.actionNotesListFragmentToNoteInfoFragment(it.id)
            )
        })

        binding.notesList.adapter = notesAdapter

        notesListViewModel.send(GetNotesListEvent())

        notesListViewModel.state.observe(viewLifecycleOwner) {
            notesAdapter.submitList(it.notesList)
            binding.emptyBlock.visibility = if (it.notesList.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.addNoteFab.setOnClickListener {
            findNavController().navigate(R.id.action_notesListFragment_to_addNoteFragment)
        }

        return binding.root
    }

}