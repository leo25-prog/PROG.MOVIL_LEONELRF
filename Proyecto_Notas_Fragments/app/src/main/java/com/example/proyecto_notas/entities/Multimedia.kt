package com.example.proyecto_notas.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "multimedia")
class Multimedia(

    @PrimaryKey(autoGenerate = true)
    val idMultimedia: Int,
    var idNote: Int,
    var idTask: Int,
    var type: String,
    val uri: String,
    val description: String
)