package com.example.proyecto_notas.data.dao

import androidx.room.*
import com.example.proyecto_notas.entities.Reminder

@Dao
interface ReminderDao{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(reminder: Reminder)

    @Query("SELECT * FROM Reminder WHERE taskID=:id")
    fun getAllReminders(id: Int): MutableList<Reminder>

    @Query("DELETE FROM Reminder WHERE taskID = :id")
    fun deleteAllReminders(id: Int)

    @Delete
    fun deleteReminder(reminder: Reminder)

    @Query("SELECT MAX(ID) FROM reminder")
    fun getMaxId(): Int

}
