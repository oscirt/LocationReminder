package com.example.locationreminder.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecase.GetNotesUseCase
import com.example.locationreminder.presentation.viewmodel.event.GetNotesListEvent
import com.example.locationreminder.presentation.viewmodel.event.NotesListFragmentEvent
import com.example.locationreminder.presentation.viewmodel.state.NotesListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase
) : ViewModel() {

    private var _state = MutableLiveData<NotesListState>()
    val state: LiveData<NotesListState> get() = _state

    init {
        _state.value = NotesListState(notesList = listOf())
    }

    fun send(event: NotesListFragmentEvent) {
        when (event) {
            is GetNotesListEvent -> {
                loadListFromDatabase()
            }
        }
    }

    private fun loadListFromDatabase() {
        viewModelScope.launch {
            val list = getNotesUseCase.execute()
            _state.postValue(NotesListState(list))
        }
    }
}