package com.example.proyecto_notas.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.proyecto_notas.entities.Multimedia

@Dao
interface MultimediaDao {
    @Insert
    fun insert(multimedia: Multimedia): Long

    @Query("SELECT * FROM multimedia where idNote =:idNote")
    fun getMultimedia(idNote: Int) : List<Multimedia>

    @Query("SELECT * FROM multimedia where idTask =:idTask")
    fun getMultimediaTask(idTask: Int) : List<Multimedia>

    @Delete
    fun delete(multimedia: Multimedia)
}