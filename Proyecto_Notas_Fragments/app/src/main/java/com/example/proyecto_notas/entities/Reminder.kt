package com.example.proyecto_notas.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity (tableName = "reminder")
data class Reminder (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var taskID: Int,
    var date: String,
    var time: String
)