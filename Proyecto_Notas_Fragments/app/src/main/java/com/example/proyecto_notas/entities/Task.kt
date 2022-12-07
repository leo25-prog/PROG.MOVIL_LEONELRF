package com.example.proyecto_notas.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id : Int,
    var title : String,
    var priority : String,
    var timestamp: Long,
    var limitDate: String,
    var limitTime: String,
):Parcelable