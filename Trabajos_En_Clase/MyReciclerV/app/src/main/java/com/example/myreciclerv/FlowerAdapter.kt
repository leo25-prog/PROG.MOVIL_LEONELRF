package com.example.myreciclerv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlowerAdapter (val dataset : Array <String>): RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder>() {
    class FlowerViewHolder (view: View) : RecyclerView.ViewHolder (view) {
        val flowerTextView : TextView
        init {
            flowerTextView = view.findViewById(R.id.flower_text)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_flower_item, parent, false)

        return FlowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int){
        holder.flowerTextView.text = dataset[position]
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}