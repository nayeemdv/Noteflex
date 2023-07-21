package com.example.noteflex.activities

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.noteflex.R
import com.example.noteflex.adapter.NotesAdapter
import com.example.noteflex.databinding.ActivityMainBinding
import com.example.noteflex.models.Note
import com.example.noteflex.models.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
            layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
            adapter = myAdapter
        }

        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddNotes::class.java)
            createNote.launch(intent)
        }
    }

    private val fab: FloatingActionButton by lazy {
        binding.fabAdd
    }

    override fun onItemClicked(note: Note) {
        val intent = Intent(this, AddNotes::class.java)
        intent.putExtra("current_note", note)
        updateNote.launch(intent)
    }
    private var count = 0
    override fun onItemLongClicked(note: Note) {
        count++
        if (count == 1){
            binding.toolbar.visibility = View.VISIBLE
            binding.toolbarHomeMenu.visibility =View.GONE
            binding.toolbar.inflateMenu(R.menu.top_bar_menu)
        }

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
            val note: Note? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getSerializableExtra("note", Note::class.java)
            } else {
                result.data?.getSerializableExtra("note") as? Note
            }
            if (note != null) {
                if (isUpdate) {
                    viewModel.updateNote(note)
                } else {
                    viewModel.insertNote(note)
                }
            }
        } else {
            Snackbar.make(binding.root, "Empty note discarded", Snackbar.LENGTH_SHORT)
                .setAnchorView(fab).show()
        }
    }
}
