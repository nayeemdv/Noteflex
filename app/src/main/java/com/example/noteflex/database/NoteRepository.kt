package com.example.noteflex.database

import androidx.lifecycle.LiveData
import com.example.noteflex.models.Note

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun insertMultipleNotes(selectedNotes: List<Note>) {
        for (note in selectedNotes) {
            noteDao.insert(note)
        }
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun deleteMultipleNotes(selectedNotes: List<Note>) {
        for (note in selectedNotes) {
            noteDao.delete(note)
        }
    }

    suspend fun update(note: Note) {
        noteDao.update(note.id, note.title, note.note)
    }
}