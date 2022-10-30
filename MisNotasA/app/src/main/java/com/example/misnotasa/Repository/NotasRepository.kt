package com.example.misnotasa.Repository

import androidx.annotation.WorkerThread
import com.example.misnotasa.Data.Dao.NotaDao
import com.example.misnotasa.Data.Model.Nota
import kotlinx.coroutines.flow.Flow

class NotasRepository (private val notaDao: NotaDao) {
    val allNotas: Flow<List<Nota>> = notaDao.getAllOrder()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertarAsync(nota: Nota){
        notaDao.insertAsync(nota)
    }

}