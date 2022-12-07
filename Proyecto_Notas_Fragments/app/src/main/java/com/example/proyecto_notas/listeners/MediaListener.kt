package com.example.proyecto_notas.listeners

import com.example.proyecto_notas.entities.Multimedia

interface MediaListener {
    fun onMediaClicked(media : Multimedia?, position : Int)
}