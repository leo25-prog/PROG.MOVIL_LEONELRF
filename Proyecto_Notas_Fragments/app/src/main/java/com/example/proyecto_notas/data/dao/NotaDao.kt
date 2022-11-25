package com.example.proyecto_notas

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto_notas.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {

    @Query("select * from notes ORDER BY uid DESC")
    fun getAllNotes() : List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertNote(nota: Note)

    @Delete
    fun delete(nota: Note)

    @Update
    fun update(nota: Note)

    @Query("SELECT * FROM notes WHERE uid = :userId")
    fun getOneById(userId: Int): Note

    @Query("select * from notes order by date_time desc")
    fun getAllOrder() : Flow<List<Note>>

    @Query("DELETE FROM notes")
    suspend fun deleteAll() : Int

}