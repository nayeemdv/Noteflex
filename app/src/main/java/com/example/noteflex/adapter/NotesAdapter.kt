package com.example.noteflex.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.noteflex.databinding.ItemsListBinding
import com.example.noteflex.models.Note

class NotesAdapter(private val listener: NotesClickListener) :
    RecyclerView.Adapter<NotesAdapter.NotesViewHolder>() {

    private val notesList = ArrayList<Note>()
    private val fullList = ArrayList<Note>()
    private var isSelect =false
    private var count = 0


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
                isSelect = count != 0
                if (!isSelect){
                    listener.onItemClicked(notesList[holder.adapterPosition])
                }else{
                    transitionItems(it)
                }

            }
            cardLayout.setOnLongClickListener {
                transitionItems(it)
                listener.onItemLongClicked(notesList[holder.adapterPosition])
                true
            }
        }
    }

    private fun transitionItems(view: View) {
        if (!view.isSelected){
            view.isSelected = true
            count++
        }else{
            view.isSelected = false
            count--
        }
    }


    fun updateList(newList: List<Note>) {
        fullList.clear()
        fullList.addAll(newList)

        notesList.clear()
        notesList.addAll(fullList)
        notifyDataSetChanged()
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
        fun onItemClicked(note: Note)
        fun onItemLongClicked(note: Note)
    }


}