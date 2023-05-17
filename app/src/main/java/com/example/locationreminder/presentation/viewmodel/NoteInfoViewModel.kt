package com.example.locationreminder.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.GetNoteByIdUseCase
import com.example.locationreminder.presentation.viewmodel.event.GetNoteById
import com.example.locationreminder.presentation.viewmodel.event.NoteInfoFragmentEvent
import com.example.locationreminder.presentation.viewmodel.state.NoteInfoState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteInfoViewModel @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase
) : ViewModel() {

    private var _state = MutableLiveData<NoteInfoState>()
    val state: LiveData<NoteInfoState> get() = _state

    init {
        _state.value = NoteInfoState(
            id = -1,
            name = "",
            description = "",
            placeCoordinates = ""
        )
    }

    fun send(event: NoteInfoFragmentEvent) {
        when (event) {
            is GetNoteById -> {
                loadNoteById(event.id)
            }
        }
    }

    private fun loadNoteById(id: Long) {
        viewModelScope.launch {
            val note = getNoteByIdUseCase.execute(id)
            _state.value = NoteInfoState(
                id = note.id,
                name = note.name,
                description = note.description,
                placeCoordinates = note.placeCoordinates
            )
        }
    }

}