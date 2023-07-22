package com.example.noteflex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.noteflex.databinding.ItemsListBinding
import com.example.noteflex.models.Note

class NotesAdapter(private val listener: NotesClickListener) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val notesList = ArrayList<Note>()
    private val fullList = ArrayList<Note>()
    private val selectedNote = mutableListOf<Note>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotesViewHolder {
        return NotesViewHolder(
            ItemsListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: NotesViewHolder, position: Int) {
        val currentNotes = notesList[position]

        holder.binding.apply {
            if (currentNotes.title.isEmpty()) {
                tvTitle.visibility = View.GONE
            } else {
                tvTitle.visibility = View.VISIBLE
                tvTitle.text = currentNotes.title
            }

            if (currentNotes.note.isEmpty()) {
                tvNotes.visibility = View.GONE
            } else {
                tvNotes.visibility = View.VISIBLE
                tvNotes.text = currentNotes.note
            }


            cardLayout.setOnClickListener {
                if (selectedNote.isEmpty()) {
                    listener.onItemClicked(notesList[holder.adapterPosition], 0)
                } else {
                    isSelected(it, currentNotes)
                    listener.onItemClicked(notesList[holder.adapterPosition], selectedNote.size)
                }

            }
            cardLayout.setOnLongClickListener {
                isSelected(it, currentNotes)
                listener.onItemLongClicked(notesList[holder.adapterPosition], selectedNote.size)
                true
            }
        }
    }

    private fun isSelected(view: View, note: Note) {
        if (selectedNote.contains(note)) {
            view.isSelected = false
            selectedNote.remove(note)
        } else {
            view.isSelected = true
            selectedNote.add(note)
        }
    }

    fun getSelectedNotes(): List<Note> {
        return selectedNote.toList().also {
            selectedNote.clear()
            notifyDataSetChanged()
        }
    }


    fun updateList(newList: List<Note>) {
        val diffResult = DiffUtil.calculateDiff(NotesDiffCallback(notesList, newList))
        notesList.clear()
        notesList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    private class NotesDiffCallback(
        private val oldList: List<Note>,
        private val newList: List<Note>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldList[oldItemPosition] == newList[newItemPosition]
    }


    fun filterList(search: String) {
        notesList.clear()
        for (item in fullList) {
            if (item.title.lowercase().contains(search.lowercase()) || item.note.lowercase()
                    .contains(search.lowercase())
            ) {
                notesList.add(item)
            }
        }
        notifyDataSetChanged()
    }


    inner class NotesViewHolder(val binding: ItemsListBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface NotesClickListener {
        fun onItemClicked(note: Note, count: Int)
        fun onItemLongClicked(note: Note, count: Int)
    }


}