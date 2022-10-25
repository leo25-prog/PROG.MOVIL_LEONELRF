package com.example.misnotasa

import android.app.Application
import com.example.misnotasa.Data.Database.MisNotasDataBase
import com.example.misnotasa.Repository.NotasRepository

class MisNotasApplication : Application() {
    val database by lazy {MisNotasDataBase.getDatabase(this)}
    val repository by lazy {NotasRepository(database.notaDao())}



}