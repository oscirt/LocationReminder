package com.example.locationreminder.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.domain.models.Note
import com.example.locationreminder.databinding.ItemNoteBinding

class NotesRecyclerViewAdapter(
    private val noteClickListener: NoteClickListener,
    private val checkboxClickListener: NoteClickListener
) : ListAdapter<Note, NotesRecyclerViewAdapter.NoteViewHolder>(DiffCallback){

    class NoteViewHolder(private val binding: ItemNoteBinding) : ViewHolder(binding.root) {
        fun bind(note: Note, clickListener: NoteClickListener,
                 checkboxClickListener: NoteClickListener) {
            binding.note = note
            binding.clickListener = clickListener
            binding.checkboxClickListener = checkboxClickListener
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            ItemNoteBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        holder.bind(
            note = note,
            clickListener = noteClickListener,
            checkboxClickListener = checkboxClickListener
        )
    }


    private object DiffCallback : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem == newItem
        }
    }

}