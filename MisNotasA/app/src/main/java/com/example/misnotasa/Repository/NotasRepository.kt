package com.example.misnotasa.Repository

import androidx.annotation.WorkerThread
import com.example.misnotasa.Data.Dao.NotaDao
import com.example.misnotasa.Data.Model.Nota
import kotlinx.coroutines.flow.Flow

class NotasRepository (private val notaDao: NotaDao) {
    val allNotas: Flow<List<Nota>> = notaDao.getAllOrder()

    fun insertar(nota: Nota){
        notaDao.insert(nota)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertarAsync(nota: Nota){
        notaDao.insert(nota)
    }


}