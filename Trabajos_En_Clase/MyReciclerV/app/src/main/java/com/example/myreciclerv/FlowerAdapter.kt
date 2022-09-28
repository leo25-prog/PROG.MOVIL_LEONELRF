package com.example.myreciclerv

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myreciclerv.data.Flower.Flower

class FlowerAdapter (val dataset : List <Flower>, val onClick: (Flower) -> Unit): RecyclerView.Adapter<FlowerAdapter.FlowerViewHolder>() {
    class FlowerViewHolder (view: View, onClick: (Flower) -> Unit) : RecyclerView.ViewHolder (view) {
        val flowerTextView : TextView
        val flowerImageView : ImageView
        var currentFlower : Flower?= null
        init {
            view.setOnClickListener{
                currentFlower?.let {
                    onClick(it)
                }
            }
            flowerTextView = view.findViewById(R.id.flower_text)
            flowerImageView = view.findViewById(R.id.flower_image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_flower_adapter_item, parent, false)

        return FlowerViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int){
        holder.currentFlower = dataset[position]
        holder.flowerTextView.text = dataset[position].name
        holder.flowerImageView.setImageResource(
            dataset[position].image?:R.drawable.ic_launcher_foreground
        )
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}