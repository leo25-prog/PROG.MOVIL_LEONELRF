package com.example.myreciclerv.Fragmentos

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myreciclerv.DataSource
import com.example.myreciclerv.FlowerAdapter
import com.example.myreciclerv.MainActivity
import com.example.myreciclerv.R
import com.example.myreciclerv.data.Flower.flowerList

class FragmentFlowerList : Fragment(R.layout.layout_fragment_listflower) {
    lateinit var rvlf: RecyclerView
    lateinit var contexto: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        contexto = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        SavedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.layout_fragment_listflower, container, false)
        rvlf = layout.findViewById<RecyclerView>(R.id.recyclerView)

        rvlf.layoutManager = LinearLayoutManager(contexto, LinearLayoutManager.VERTICAL, false)

        val adaptador = FlowerAdapter(
            DataSource.lsFlower,
            {
                Toast.makeText(
                    context,
                    "Flor presionada ${it.name}",
                    Toast.LENGTH_SHORT
                ).show()

                val act = activity as MainActivity
                act.mostrarDetailFlower(it)
            }
        )

        rvlf.adapter = adaptador

        return layout
    }

    fun updateLs() {
        rvlf.adapter?.notifyDataSetChanged()
    }
}