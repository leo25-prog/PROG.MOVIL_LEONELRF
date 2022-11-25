package com.example.proyecto_notas.listeners

import com.example.proyecto_notas.entities.Note

interface NoteListener {
    fun onNoteClicked(note : Note?, position : Int)
}