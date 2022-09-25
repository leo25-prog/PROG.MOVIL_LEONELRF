package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class RegisterActivity : AppCompatActivity(), OnItemClickListener, OnItemSelectedListener{

    lateinit var txtEmail: EditText
    lateinit var txtName: EditText
    lateinit var btnSave: Button
    lateinit var spnSex: Spinner
    lateinit var arrSex : Array <String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_register)

        //PARA INICIALIZAR UN CONTROL DE DATOS SE NENECESITAN LOS SIGUIENTES ELEMENTOS:
        //1) Una coleccion
        //2) Un adaptar
        //3) Y un control de datos
        //4) ASIGNAR A CONTROL DE DATOS EL ADAPTADOR
        //5) ASIGNAR EVENTO DE SELECCION DE UN ELEMENTO

        //Coleccion
        arrSex = resources.getStringArray(R.array.sexo)

        //Adaptador
        val adapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_list_item_1,
            android.R.id.text1,
            arrSex
        )

        //Control de datos
        spnSex = findViewById(R.id.spnSexo)

        //Asignacion de adaptador
        spnSex.adapter = adapter

        //spnSex.onItemSelectedListener = this

        //val onIS = object  : AdapterView.OnItemSelectedListener{
        //            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        //                Toast.makeText(applicationContext, arrSex[p2],Toast.LENGTH_LONG).show()
        //            }
        //
        //            override fun onNothingSelected(p0: AdapterView<*>?) {
        //
        //            }
        //        }
        //spnSex.onItemSelectedListener = onIS

        //RECUPERAR ELEMENTO SELECCIONADO
        spnSex.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                Toast.makeText(applicationContext, arrSex[p2],Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

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

            setResult(RESULT_OK, datos)
            finish()
        }
    }

    override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        //TODO("Not yet implemented")
        Log.d("SPINNER", "Element selected ${arrSex[p2]}")
        Toast.makeText(applicationContext, "Element selected ${arrSex[p2]}", Toast.LENGTH_LONG).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}