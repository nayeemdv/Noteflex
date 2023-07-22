package com.example.noteflex.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.noteflex.database.NoteDatabase
import com.example.noteflex.database.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>

    init {
        val dao = NoteDatabase.getDatabase(application).getNoteDao()
        repository = NoteRepository(dao)
        allNotes = repository.allNotes
    }

    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(note)
    }
    fun insertMultipleNotes(selectedNotes: List<Note>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.insertMultipleNotes(selectedNotes)
            }
        }
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(note)
    }

    fun deleteMultipleNotes(selectedNotes: List<Note>) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteMultipleNotes(selectedNotes)
            }
        }
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(note)
    }
}