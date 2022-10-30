package com.example.misnotasa

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.misnotasa.Data.Database.MisNotasDataBase
import com.example.misnotasa.Data.Model.Nota
import com.example.misnotasa.Repository.NotaListAdapter
import com.example.misnotasa.Repository.NotaViewModel
import com.example.misnotasa.Repository.NotaViewModelFactory


class MainActivity : AppCompatActivity() {
    private val notaViewModel: NotaViewModel by  viewModels {
        NotaViewModelFactory((application as MisNotasApplication).repository)
    }

    private lateinit var db: MisNotasDataBase
    private lateinit var btnI: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*db =
            MisNotasDataBase.getDatabase(applicationContext)*/

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter = NotaListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        notaViewModel.allNotas.observe( this) {
            notas -> notas.let {
                adapter.submitList(it)
                for (ele in  it){
                    Log.d("ACTUA", ele.titulo)
                }
            }
        }

        btnI = findViewById<Button>(R.id.buttonInsert)
        btnI.setOnClickListener {
            notaViewModel.cont++
            notaViewModel.insertarAsync(Nota(0,"Nueva inserción ${notaViewModel.cont} ","des",1,null,false))
            /*MisNotasDataBase.databaseexecutor.execute {
                val dao = db.notaDao()

                val nota = Nota(0,"Mi primer nota",
                    "Registro", 0, null,false)

                dao.insert(nota)

                val lsN = dao.getAll()

                for (item in lsN){
                    Log.i("NOTASX", "${item.uid}  ${item.titulo}")
                }

            }*/
        }
    }
}