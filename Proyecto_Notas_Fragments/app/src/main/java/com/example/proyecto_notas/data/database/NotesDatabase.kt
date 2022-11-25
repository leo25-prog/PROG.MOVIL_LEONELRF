package com.example.proyecto_notas

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.proyecto_notas.entities.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = [Note::class], version = 1, exportSchema = false)
 abstract class NotesDataBase: RoomDatabase() {
    abstract fun noteDao(): NotaDao?

    companion object {
        private var notesDatabase: NotesDataBase? = null
        @Synchronized
        fun getDatabase(context: Context?): NotesDataBase? {
            if (notesDatabase == null) {
                notesDatabase = Room.databaseBuilder(
                    context!!,
                    NotesDataBase::class.java,
                    "notes_db"
                ).build()
            }
            return notesDatabase
        }
    }
}