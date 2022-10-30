package com.example.misnotasa

import android.app.Application
import com.example.misnotasa.Data.Database.MisNotasDataBase
import com.example.misnotasa.Repository.NotasRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class MisNotasApplication : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { MisNotasDataBase.getDatabase(this, applicationScope) }
    val repository by lazy { NotasRepository(database.notaDao()) }

}