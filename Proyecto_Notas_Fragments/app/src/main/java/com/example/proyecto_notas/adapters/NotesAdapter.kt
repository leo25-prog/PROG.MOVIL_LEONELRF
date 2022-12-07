package com.example.proyecto_notas.adapters

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Looper
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.example.proyecto_notas.R
import android.widget.TextView
import android.widget.Toast
import com.example.proyecto_notas.Notas.Notas_Fragment
import com.example.proyecto_notas.entities.Note
import com.example.proyecto_notas.listeners.NoteListener
import com.makeramen.roundedimageview.RoundedImageView
import java.util.*
import java.util.logging.Handler

class NotesAdapter(private var notes: List<Note>, private val notesListener: NoteListener) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    private var timer : Timer? = null
    private var notesSource = notes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_note,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.setNote(notes[position])
        holder.layoutNote.setOnClickListener{
            notesListener.onNoteClicked(notes[position], position)
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textTitle: TextView
        var textSubtitle: TextView
        var textDateTime: TextView
        var layoutNote : LinearLayout

        init {
            textTitle = itemView.findViewById(R.id.textTitle)
            textSubtitle = itemView.findViewById(R.id.textSubtitle)
            textDateTime = itemView.findViewById(R.id.textDateTime)
            layoutNote = itemView.findViewById(R.id.layoutNote)
        }

        fun setNote(note: Note) {
            textTitle.text = note.title
            if (note.subtitle.trim { it <= ' ' }.isEmpty()) {
                textSubtitle.visibility = View.GONE
            } else {
                textSubtitle.text = note.subtitle
            }
            textDateTime.text = note.dateTime

            val gradientDrawable = layoutNote!!.background as GradientDrawable
            if(note.color != null){
                gradientDrawable.setColor(Color.parseColor(note.color))
            }
            else{
                gradientDrawable.setColor(Color.parseColor("#333333"))
            }
        }
    }

    fun searchNotes(searchKeyword: String) {
        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                if (searchKeyword.trim().isEmpty()) {
                    notes = notesSource
                } else {
                    val temp = ArrayList<Note>()
                    for (note in notesSource) {
                        if (note.title.lowercase(Locale.getDefault()).contains(
                                searchKeyword.lowercase(
                                    Locale.getDefault()
                                )
                            )
                            || note.subtitle.lowercase(Locale.getDefault()).contains(
                                searchKeyword.lowercase(
                                    Locale.getDefault()
                                )
                            )
                            || note.noteText.lowercase(Locale.getDefault()).contains(
                                searchKeyword.lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            temp.add(note)
                        }
                    }
                    notes = temp
                }
                android.os.Handler(Looper.getMainLooper()).post { notifyDataSetChanged() }
            }
        }, 500)
    }

    fun cancelTimer(){
        if(timer != null){
            timer!!.cancel()
        }
    }
}