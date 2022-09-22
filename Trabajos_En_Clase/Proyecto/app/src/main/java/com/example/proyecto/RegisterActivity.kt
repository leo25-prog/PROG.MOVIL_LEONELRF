package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class RegisterActivity : AppCompatActivity(), AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {
    lateinit var txtEmail: EditText
    lateinit var txtName: EditText
    lateinit var btnSave: Button
    lateinit var spnSex: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_register)

        val arrSex = resources.getStringArray(R.array.sexo)

        val adapter = ArrrayAdapter(
            applicationContext,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            arrSex
        )

        spnSex = findViewById(R.id.spnSexo);
        spnSex.adapter = adapter
        spnSex.onItemSelectedListener = this

        val onIS = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {


            }
        }

        spnSex =

        //RECUPERAR PARAMETROS QUE SE ENVIAN DESDE OTRA ACTIVIDAD
        val titulo = intent.getStringExtra("par2")
        val accion = intent.getIntExtra("par1", -1)

        //PROPIEDAD TITLE DE LA ACTIVIDAD
        title = titulo
        Toast.makeText(applicationContext, "Accion: $accion", Toast.LENGTH_LONG).show()

        txtEmail = findViewById(R.id.txtEmail)
        txtName = findViewById(R.id.txtName)
        btnSave = findViewById(R.id.btnSaveRegister)

        btnSave.setOnClickListener {
            val datos = Intent()
            datos.putExtra("email", txtEmail.text.toString())
            datos.putExtra("name", txtName.text.toString())
            Toast.makeText(
                applicationContext,
                spnSex.selectedItem.toString(),
                Toast.LENGTH_LONG
            ).show()
            spnSex.selectedItem
            setResult(RESULT_OK, datos)
            finish()
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //TODO("Not yet implemented")
        Log.d("SPINNER", "Elemento selected $arrSex[p2]")
        Toast.makeText(applicationContext, "Elemento selected $arrSex[p2]", Toast.makeText()).show()
        arrSex[p2]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}