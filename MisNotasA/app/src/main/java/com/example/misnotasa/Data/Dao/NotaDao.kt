package com.example.misnotasa.Data.Dao

import androidx.room.*
import com.example.misnotasa.Data.Model.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Insert
    suspend fun insertAsync(vararg nota : Nota)

    @Update
    fun update(nota: Nota)

    @Delete
    fun delete(nota: Nota)

    @Query("select * from Nota")
    fun getAll() : List <Nota>

    @Query("select * from Nota where uid = :userId")
    fun getOneById(userId: Int) : Nota

    @Query("select * from Nota order by fecha desc")
    fun getAllOrder() : Flow<List<Nota>>

    @Query("delete from Nota")
    suspend fun deleteAll() : Int


}