package com.example.proyecto_notas

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyecto_notas.data.dao.MultimediaDao
import com.example.proyecto_notas.data.dao.ReminderDao
import com.example.proyecto_notas.data.dao.TaskDao
import com.example.proyecto_notas.entities.Multimedia
import com.example.proyecto_notas.entities.Note
import com.example.proyecto_notas.entities.Reminder
import com.example.proyecto_notas.entities.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = [Note::class, Multimedia::class, Task::class, Reminder::class], version = 1, exportSchema = false)
 abstract class NotesDataBase: RoomDatabase() {
    abstract fun noteDao(): NotaDao?
    abstract fun multimediaDao(): MultimediaDao?
    abstract fun taskDao(): TaskDao?
    abstract fun reminderDao() : ReminderDao

    companion object {
        private var notesDatabase: NotesDataBase? = null
        @Synchronized
        fun getDatabase(context: Context?): NotesDataBase? {
            if (notesDatabase == null) {
                notesDatabase = Room.databaseBuilder(
                    context!!,
                    NotesDataBase::class.java,
                    "notes_db"
                ).allowMainThreadQueries().build()
            }
            return notesDatabase
        }
    }
}