package com.example.roller

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    lateinit var boton : Button
    lateinit var lbl : TextView
    lateinit var img : ImageView
    lateinit var imgDos : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_roller)

        lbl = findViewById(R.id.lblSaludo)
        boton = findViewById(R.id.btnRoller)
        img = findViewById(R.id.imgRoller)

        imgDos = findViewById(R.id.imgRollerDos)

        img.setImageDrawable(resources.getDrawable((R.drawable.dice_3)))
        imgDos.setImageDrawable(resources.getDrawable((R.drawable.dice_3)))

        boton.setOnClickListener {
            val al = getRandomDiceImage()
            Toast.makeText(applicationContext, "Boton presionado aleatorio", Toast.LENGTH_SHORT).show()

            val idImagenAl = when (al){
                1 -> R.drawable.dice_1
                2 -> R.drawable.dice_2
                3 -> R.drawable.dice_3
                4 -> R.drawable.dice_4
                5 -> R.drawable.dice_5
                6 -> R.drawable.dice_6
                else -> R.drawable.empty_dice
            }

            val ale = getRandomDiceImage()

            val idImagenAle = when (ale){
                1 -> R.drawable.dice_1
                2 -> R.drawable.dice_2
                3 -> R.drawable.dice_3
                4 -> R.drawable.dice_4
                5 -> R.drawable.dice_5
                6 -> R.drawable.dice_6
                else -> R.drawable.empty_dice
            }

            lbl.text = "Dado uno: " + al.toString() + "\n" +
                       "Dado dos: " + ale.toString() + "\n" +
                       "Total: " + (al + ale).toString()

            img.setImageResource(idImagenAl)
            imgDos.setImageResource(idImagenAle)
        }
    }

    private fun getRandomDiceImage() : Int {
        return (1..6).random()
    }

    fun diceRoller () : Int{
        return (1..6).random()
    }
}