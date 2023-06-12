package com.example.locationreminder.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Note
import com.example.domain.usecase.SaveNoteUseCase
import com.example.locationreminder.presentation.viewmodel.event.AddNoteEvent
import com.example.locationreminder.presentation.viewmodel.event.AddNoteFragmentEvent
import com.example.locationreminder.presentation.viewmodel.event.SaveInputsEvent
import com.example.locationreminder.presentation.viewmodel.state.AddNoteState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    private val saveNoteUseCase: SaveNoteUseCase
) : ViewModel() {

    private var _state = MutableLiveData<AddNoteState>()
    val state: LiveData<AddNoteState> get() = _state

    init {
        _state.value = AddNoteState(
            id = -1,
            name = "",
            description = "",
            placeName = "",
            latitude = .0,
            longitude = .0
        )
    }

    fun send(event: AddNoteFragmentEvent) {
        when (event) {
            is AddNoteEvent -> addNote(event.note)
            is SaveInputsEvent -> saveInput(name = event.name, description = event.description)
        }
    }

    private fun addNote(note: Note) {
        viewModelScope.launch {
            val id = saveNoteUseCase.execute(note)
            _state.value = AddNoteState(
                id = id,
                name = note.name,
                description = note.description,
                placeName = note.placeName,
                latitude = note.latitude,
                longitude = note.longitude
            )
        }
    }

    private fun saveInput(name: String, description: String) {
        val oldState = _state.value!!
        _state.value = AddNoteState(
            id = oldState.id,
            name = name,
            description = description,
            placeName = oldState.placeName,
            latitude = oldState.latitude,
            longitude = oldState.longitude
        )
    }
}