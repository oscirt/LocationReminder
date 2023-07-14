package com.example.locationreminder.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Note
import com.example.domain.usecase.CheckNoteUseCase
import com.example.domain.usecase.GetNotesUseCase
import com.example.locationreminder.presentation.viewmodel.event.CheckerNoteListEvent
import com.example.locationreminder.presentation.viewmodel.event.GetNotesListEvent
import com.example.locationreminder.presentation.viewmodel.event.NotesListFragmentEvent
import com.example.locationreminder.presentation.viewmodel.state.NotesListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase,
    private val checkNoteUseCase: CheckNoteUseCase
) : ViewModel() {

    private var _state = MutableLiveData<NotesListState>()
    val state: LiveData<NotesListState> get() = _state

    init {
        _state.value = NotesListState(notesList = listOf())
    }

    fun send(event: NotesListFragmentEvent) {
        when (event) {
            is GetNotesListEvent -> loadListFromDatabase()
            is CheckerNoteListEvent -> checkNote(event.note)
        }
    }

    private fun loadListFromDatabase() {
        viewModelScope.launch {
            val list = getNotesUseCase.execute()
            _state.postValue(NotesListState(list))
        }
    }

    private fun checkNote(note: Note) {
        viewModelScope.launch {
            val mut = _state.value!!.notesList.map { it.copy() }
            mut[mut.indexOf(note)].isChecked = note.isChecked.not()
            _state.postValue(NotesListState(mut.toList()))
            checkNoteUseCase.execute(note)
        }
    }
}