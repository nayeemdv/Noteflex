package com.example.noteflex.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import com.example.noteflex.R
import com.example.noteflex.databinding.ActivityAddNotesBinding
import com.example.noteflex.models.Note

class AddNotes : AppCompatActivity() {
    private lateinit var binding: ActivityAddNotesBinding
    private lateinit var note: Note
    private lateinit var oldNote: Note
    private var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.white)

        try {
            oldNote = intent.getSerializableExtra("current_note") as Note
            binding.etTitle.setText(oldNote.title)
            binding.etNotes.setText(oldNote.note)
            isUpdate = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveNoteAndFinish()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
        binding.ivBack.setOnClickListener {
            saveNoteAndFinish()
        }
    }

    fun saveNoteAndFinish() {
        val title = binding.etTitle.text.toString()
        val notes = binding.etNotes.text.toString()

        if (title.isEmpty() && notes.isEmpty()) {
            setResult(RESULT_CANCELED)
            finish()
        } else {
            note = if (isUpdate) Note(oldNote.id, title, notes) else Note(null, title, notes)

            val intent = Intent().apply {
                putExtra("note", note)
            }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}