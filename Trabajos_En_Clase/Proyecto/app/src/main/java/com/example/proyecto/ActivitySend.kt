package com.example.proyecto

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ActivitySend : AppCompatActivity() {
    lateinit var txtTexto : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        txtTexto = findViewById(R.id.txtText)
        val texto = intent.getStringExtra(Intent.EXTRA_TEXT)
        txtTexto.text = texto
    }
}