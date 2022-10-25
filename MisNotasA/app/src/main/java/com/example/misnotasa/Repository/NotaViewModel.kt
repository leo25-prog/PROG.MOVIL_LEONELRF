package com.example.misnotasa.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.misnotasa.Data.Model.Nota
import kotlinx.coroutines.coroutineScope

class NotaViewModel (private val repository: NotasRepository): ViewModel() {
    val allNotas : LiveData <List <Nota> > = repository.allNotas as LiveData <List <Nota> >

    fun insertarAsync (nota: Nota) = viewModelScope.launch {
        repository.insertarAsync(nota)
    }


}

class NotaViewModelFactory(private val respository: NotasRepository) : viewModelProvider.Factory{
    @override fun <T : ViewModel> create(modelClass )
}