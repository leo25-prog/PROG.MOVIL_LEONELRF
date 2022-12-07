package com.example.proyecto_notas.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_notas.R
import com.example.proyecto_notas.entities.Multimedia
import com.example.proyecto_notas.listeners.MediaListener

class MediaAdapter (private var media: List<Multimedia>, private val mediaListener: MediaListener) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        return MediaViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_container_media,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        holder.setMedia(media[position])
        holder.layoutMedia.setOnClickListener{
            mediaListener.onMediaClicked(media[position], position)
        }
    }

    override fun getItemCount(): Int {
        return media.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView : ImageView
        var videoView : VideoView
        var description : TextView
        var layoutMedia : LinearLayout

        init {
            imageView = itemView.findViewById(R.id.imageView)
            videoView = itemView.findViewById(R.id.videoView)
            description = itemView.findViewById(R.id.textDescription)
            layoutMedia = itemView.findViewById(R.id.layoutMedia)
        }

        fun setMedia(media: Multimedia) {
            if(media.type == "photo"){
                imageView.setImageURI(media.uri.toUri())
                imageView.visibility = View.VISIBLE
            }
            else{
                videoView.setVideoURI(media.uri.toUri())
                videoView.visibility = View.VISIBLE
                videoView.start()
            }
            description.text = media.description
        }
    }
}