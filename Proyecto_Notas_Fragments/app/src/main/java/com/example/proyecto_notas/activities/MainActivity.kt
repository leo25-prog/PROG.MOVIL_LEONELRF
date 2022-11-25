package com.example.proyecto_notas.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_notas.Notas.Notas_Fragment
import com.example.proyecto_notas.NotesDataBase
import com.example.proyecto_notas.R
import com.example.proyecto_notas.Tareas.Tareas_Fragment
import com.example.proyecto_notas.adapters.NotesAdapter
import com.example.proyecto_notas.databinding.ActivityMainBinding
import com.example.proyecto_notas.entities.Note
import com.example.proyecto_notas.listeners.NoteListener

class MainActivity : AppCompatActivity(){

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Notas_Fragment())

        binding.topNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.note -> replaceFragment(Notas_Fragment())
                R.id.task -> replaceFragment(Tareas_Fragment())
                else -> { }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
