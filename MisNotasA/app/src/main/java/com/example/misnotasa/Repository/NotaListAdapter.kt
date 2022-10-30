package com.example.misnotasa.Repository

import com.example.misnotasa.Data.Model.Nota
import com.example.misnotasa.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class NotaListAdapter() :  ListAdapter<Nota, NotaListAdapter.NotaViewHolder> (NotasComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        return NotaViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NotaViewHolder(itemView: View) : ViewHolder(itemView) {
        private val txtTitulo : TextView = itemView.findViewById(   R.id.textView)

        fun bind (nota: Nota){
            txtTitulo.text = nota.titulo
        }
        companion object {
            fun create(parent: ViewGroup): NotaViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_recyclerview_item, parent, false)
                return NotaViewHolder(view)
            }
        }
    }

    class NotasComparator : DiffUtil.ItemCallback<Nota>(){
        override fun areItemsTheSame(oldItem: Nota, newItem: Nota): Boolean {
            return oldItem.uid==newItem.uid
        }

        override fun areContentsTheSame(oldItem: Nota, newItem: Nota): Boolean {
            return oldItem == newItem
        }

    }
}