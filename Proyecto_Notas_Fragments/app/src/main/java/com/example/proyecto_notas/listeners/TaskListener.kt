package com.example.proyecto_notas.listeners

import com.example.proyecto_notas.entities.Task

interface TaskListener {
    fun onTaskClicked(task : Task?, position : Int)
}