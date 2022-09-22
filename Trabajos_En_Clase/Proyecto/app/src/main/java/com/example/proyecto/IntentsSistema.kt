package com.example.proyecto

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.startActivity

class IntentsSistema : AppCompatActivity() {
    lateinit var btnI1 : Button
    lateinit var btnI2 : Button
    lateinit var btnI3 : Button
    lateinit var actResLaun : ActivityResultLauncher <String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intents_sistema)

        btnI1 = findViewById(R.id.btnIntent1)
        btnI2 = findViewById(R.id.btnIntent2)
        btnI3 = findViewById(R.id.btnSend)

        btnI1.setOnClickListener {

            //URI: SON CADENAS QUE INDICAN LA UBICACION DE UN RECURSO
            //O UNA ACCION A ESPECIFICAR SOBRE UN RECURSO

            val action_dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:4451002345"))

            startActivity(action_dial)
        }

        btnI2.setOnClickListener {
            val action_view = Intent(
                Intent.ACTION_VIEW,
                //Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California")
                Uri.parse("http://sicenet.itsur.edu.mx")
            )
            startActivity(action_view)
        }

        btnI3.setOnClickListener {
            val intent_send = Intent(Intent.ACTION_SEND).apply {
            //val intent_send = Intent("com.example.proyecto.ACTIVITY_SEND").apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_EMAIL, arrayOf("jan@example.com"))
                putExtra(Intent.EXTRA_SUBJECT, "Email subject")
                putExtra(Intent.EXTRA_TEXT, "Email message text")
            }
            //startActivity(intent_send )
            actResLaun.launch("audio/*")
        }

        actResLaun = registerForActivityResult(
            ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                Toast.makeText(
                    applicationContext, "Uri: ${it.toString()}", Toast.LENGTH_LONG
                ).show()
                Log.e("ESTETAG", "Uri: ${it.toString()}")
            }
        )
    }

    override fun onStart(){
        super.onStart()
        Log.i("CICLOVIDA", "Paso por onStart")
    }

    override fun onResume(){
        super.onResume()
        Log.i("CICLOVIDA", "Paso por onResume")
    }

    override fun onPause(){
        super.onPause()
        Log.i("CICLOVIDA", "Paso por onPause")
    }

    override fun onStop(){
        super.onStop()
        Log.i("CICLOVIDA", "Paso por onStop")
    }

    override fun onDestroy(){
        super.onDestroy()
        Log.i("CICLOVIDA", "Paso por onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i("CICLODEVIDA", "Paso por onRestart")
    }
}