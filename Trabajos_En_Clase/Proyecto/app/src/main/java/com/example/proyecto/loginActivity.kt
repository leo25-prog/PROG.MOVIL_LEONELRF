package com.example.proyecto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class loginActivity : AppCompatActivity() {

    lateinit var txtUser : EditText
    lateinit var txtPassword : EditText
    lateinit var btnEntrar : Button
    lateinit var btnRegistrar : Button

    //Objeto que permite mandar llamar una actividad que devolvera un resultado
    //La activiad que se invoca esta dentro de la misma App
    lateinit var activityResultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //Se registra el tipo de actividad que se invocara(contrato),
        // y se configurara la funcion callback que se dispara cuando
        // cuando la actividad que se mando llamar devuelve un resultado
        // El registro devuelve un ActivityResultLaucher , que es el ibjeto que permitira mandar llamar una actividad
        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback {
                //el atributo it contiene el resultado y los datos devueltos
                if(it.resultCode == RESULT_OK){
                    //txtUser.text = Editable.Factory.getInstance().newEditable(it?.data?.getStringExtra("email"))
                    val email = it?.data?.getStringExtra("email")
                    // it.data.getStringExtra("name")
                    Toast.makeText(applicationContext, "email: ${email}", Toast.LENGTH_LONG).show()

                    txtUser.setText(email)
                }
            }
        )

        txtUser = findViewById(R.id.txtUsuario)
        txtPassword = findViewById(R.id.txtPassword)

        btnEntrar = findViewById(R.id.btnEnter)
        btnRegistrar = findViewById(R.id.btnRegister)

        btnRegistrar.setOnClickListener {
            //Para abrir otra activity se debe:
            //Crear un Intent de un Activity
            /*
            val intent_activity = Intent(
                applicationContext, RegisterActivity::class.java
            )*/
            val intent_activity = Intent(
                applicationContext, RegisterActivity::class.java
            )

            intent_activity.putExtra("par1", 0)
            intent_activity.putExtra("par2", "Register")

            /*
            val intent_activity = Intent(
                applicationContext, RegisterActivity::class.java
            ).apply {
                this.putExtra("par1", 0)
                this.putExtra("par2", "Register")
            }
             */

            //Abiri la activity
            //startActivity(intent_activity)

            //2) actividad que devuelve parametros  se debe construir los
            // lanzar la actividad que se desea abrir configurando previamente un intent
            activityResultLauncher.launch(intent_activity)
        }
    }
}