package com.example.locationreminder.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Note
import com.example.domain.usecase.GetNoteByIdUseCase
import com.example.domain.usecase.UpdateNoteUseCase
import com.example.locationreminder.presentation.viewmodel.event.ChangePoint
import com.example.locationreminder.presentation.viewmodel.event.GetNoteById
import com.example.locationreminder.presentation.viewmodel.event.NoteUpdateFragmentEvent
import com.example.locationreminder.presentation.viewmodel.event.UpdateNote
import com.example.locationreminder.presentation.viewmodel.state.NoteUpdateState
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteUpdateViewModel @Inject constructor(
    private val getNoteByIdUseCase: GetNoteByIdUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase
) : ViewModel() {

    private var _state = MutableLiveData<NoteUpdateState>()
    val state: LiveData<NoteUpdateState> get() = _state

    init {
        _state.value = NoteUpdateState(
            id = -1,
            name = "",
            description = "",
            placeName = "",
            latitude = .0,
            longitude = .0,
            isChecked = false
        )
    }

    fun send(event: NoteUpdateFragmentEvent) {
        when (event) {
            is GetNoteById -> loadNoteInfo(event.id)
            is UpdateNote -> updateNote(event.note)
            is ChangePoint -> changePoint(event.name, event.point)
        }
    }

    private fun loadNoteInfo(id: Long) {
        viewModelScope.launch {
            val note = getNoteByIdUseCase.execute(id)
            _state.postValue(
                NoteUpdateState(
                    id = note.id,
                    name = note.name,
                    description = note.description,
                    placeName = note.placeName,
                    latitude = note.latitude,
                    longitude = note.longitude,
                    isChecked = note.isChecked
                )
            )
        }
    }

    private fun updateNote(note: Note) {
        viewModelScope.launch {
            updateNoteUseCase.execute(note)
        }
    }

    private fun changePoint(name: String, point: LatLng) {
        _state.postValue(_state.value?.copy(placeName = name, latitude = point.latitude, longitude = point.longitude))
    }

}