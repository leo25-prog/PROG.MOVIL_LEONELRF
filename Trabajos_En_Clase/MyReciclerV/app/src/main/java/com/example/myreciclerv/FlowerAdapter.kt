package com.example.myreciclerv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.TextView

class FlowerAdapter (val dataset : Array <String>): RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder>() {
    class FlowerViewHolder (view: View) : RecyclerView.ViewHolder (view) {
        val flowerTextView : TextView
        init {
            flowerTextView = view.findViewById(R.id.flower_text)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_flower_item, parent, false)

        return FlowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int){
        holder.flowerTextView.text = dataset[position]
    }

    override fun gerItemCount() : Int{
        return dataset.size
    }

}