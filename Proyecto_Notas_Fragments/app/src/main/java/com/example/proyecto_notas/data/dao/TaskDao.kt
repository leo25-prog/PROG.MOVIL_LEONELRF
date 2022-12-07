package com.example.proyecto_notas.data.dao

import android.util.Log
import androidx.room.*
import com.example.proyecto_notas.entities.Note
import com.example.proyecto_notas.entities.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY timestamp DESC")
    fun getAllTask(): List<Task>

    @Query("SELECT * FROM tasks WHERE id= :id")
    fun getById(id: Int) : Task

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTask(task: Task)

    @Delete
    fun delete(task: Task)

    @Update
    fun update(task: Task)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll() : Int
}