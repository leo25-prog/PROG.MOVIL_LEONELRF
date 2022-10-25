package com.example.misnotasa.Data.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Nota")
data class Nota  (
    @PrimaryKey(autoGenerate = true)
    val uid: Int,
    val titulo: String,
    val descripcion: String,
    val tipo: Int,
    val fecha: String?,
    val completada: Boolean
)