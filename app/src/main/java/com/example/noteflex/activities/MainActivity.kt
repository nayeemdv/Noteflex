package com.example.noteflex.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteflex.R
import com.example.noteflex.adapter.NotesAdapter
import com.example.noteflex.databinding.ActivityMainBinding
import com.example.noteflex.models.Note
import com.example.noteflex.models.NoteViewModel
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NotesAdapter.NotesClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var myAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Initialize the UI
        initUI()

        viewModel = ViewModelProvider(this)[NoteViewModel::class.java]
        viewModel.allNotes.observe(this) { list ->
            list?.let {
                myAdapter.updateList(list)
            }
        }
    }

    private fun initUI() {
        myAdapter = NotesAdapter(this)
        binding.rvNotes.apply {
            setHasFixedSize(true)
            notesViewLayout(true)
            adapter = myAdapter
        }

        binding.columnView.setOnClickListener { column ->
            notesViewLayout(column.isSelected)
            column.isSelected = !column.isSelected
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddNotes::class.java)
            createNote.launch(intent)
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_top_menu -> {
                    deleteSelectedNotes()
                    true
                }

                else -> false
            }
        }
    }

    private fun notesViewLayout(isDefault: Boolean) {
        val layoutManager: RecyclerView.LayoutManager =
            if (isDefault) {
                StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
            } else {
                LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            }
        binding.rvNotes.layoutManager = layoutManager
    }

    private var previousCount = -1
    override fun onItemClicked(note: Note, count: Int) {
        if (count == 0 && previousCount != 1) {
            val intent = Intent(this, AddNotes::class.java)
            intent.putExtra("current_note", note)
            updateNote.launch(intent)
        } else {
            topBarMenu(count)
        }
    }

    override fun onItemLongClicked(note: Note, count: Int) {
        topBarMenu(count)
    }

    private var deletedNotes: List<Note>? = null

    private fun deleteSelectedNotes() {
        val noteList = myAdapter.getSelectedNotes()
        deletedNotes = noteList
        var snackbarMessage = ""

        if (noteList.size == 1) {
            val note = noteList[0]
            viewModel.deleteNote(note)
            snackbarMessage = "Note moved to trash"
        } else if (noteList.size > 1) {
            viewModel.deleteMultipleNotes(noteList)
            snackbarMessage = "Notes moved to trash"
        }

        Snackbar.make(binding.root, snackbarMessage, Snackbar.LENGTH_LONG)
            .setAction("Undo") { undoDelete() }
            .show()
    }


    private fun undoDelete() {
        deletedNotes?.let {
            viewModel.insertMultipleNotes(it) // Restore the deleted notes back to the list
            deletedNotes = null // Clear the temporary variable
        }
    }

    private fun topBarMenu(count: Int) {
        if (count == 0) {
            binding.toolbarHomeMenu.visibility = View.VISIBLE
            binding.toolbar.menu.clear()
        } else if (count == 1 && previousCount != 2) {
            binding.toolbarHomeMenu.visibility = View.GONE
            binding.toolbar.inflateMenu(R.menu.top_bar_menu)
        }
        binding.toolbar.title = count.toString()
        previousCount = count
    }

    private val createNote =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleNoteActivityResult(result, isUpdate = false)
        }

    private val updateNote =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            handleNoteActivityResult(result, isUpdate = true)
        }

    private fun handleNoteActivityResult(result: ActivityResult, isUpdate: Boolean) {
        if (result.resultCode == Activity.RESULT_OK) {
            val note = result.data?.getSerializableExtra("note") as? Note
            if (note != null) {
                if (isUpdate) {
                    viewModel.updateNote(note)
                } else {
                    viewModel.insertNote(note)
                }
            }
        } else {
            Snackbar.make(binding.root, "Empty note discarded", Snackbar.LENGTH_SHORT)
                .setAnchorView(binding.fabAdd).show()
        }
    }
}
