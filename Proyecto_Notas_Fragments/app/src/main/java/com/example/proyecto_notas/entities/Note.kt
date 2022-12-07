package com.example.proyecto_notas.entities

import androidx.annotation.ColorInt
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    var uid: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "date_time")
    var dateTime: String,

    @ColumnInfo(name = "subtitle")
    var subtitle: String,

    @ColumnInfo(name = "note_text")
    var noteText: String,

    @ColumnInfo(name = "color")
    var color : String,

    @ColumnInfo(name = "webLink")
    var webLink : String
)


