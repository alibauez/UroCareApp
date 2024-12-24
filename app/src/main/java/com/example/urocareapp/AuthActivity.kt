package com.example.urocareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.urocareapp.modelo.Alert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {


    private lateinit var btnRegistro : Button
    private lateinit var email: EditText
    private lateinit var passwd: EditText
    private lateinit var auth: FirebaseAuth //variable de tipo FirebaseAuth para la autenticacion
    private lateinit var btnAcceder: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)



        btnRegistro = findViewById(R.id.btnRegistro)
        email = findViewById(R.id.emailEditText)
        passwd = findViewById(R.id.passwdEditText)
        auth = Firebase.auth //esto no esta en ningun sitio, simplemente lo inicializamos
        btnAcceder = findViewById(R.id.btnAcceder)
        // Función donde haremos la lógica de la autenticación
        setup()
    }

    fun setup(){
        // Aquí pondremos la lógica de los botones de autenticación
        btnRegistro.setOnClickListener {
            // Comprobar que el correo electrónico y la contraseña no estén vacíos
            if(email.text.isNotBlank() && passwd.text.isNotBlank()){
                // sí podemos registrar al usuario
                //Log.i("INFO", email.text.toString()) //esto se quita, era para comprobar
                //Log.i("INFO", passwd.text.toString())
                auth.createUserWithEmailAndPassword( //metodo concreto para la autenticacion de email y contraseña que hemos elegido
                    email.text.toString(),
                    passwd.text.toString()
                ).addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        // El registro se ha hecho de forma correcta
                        Log.i("INFO", "El usuario se ha registrado correctamente")
                        showHome(email.text.toString())
                        email.text.clear() //Para que desaparezca de la pantalla si se registra correctamente y que no se quede escrito
                        passwd.text.clear()
                    } else{
                        // Si ha habido algún fallo que aparezca un Toast
                        //Toast.makeText(this,"Fallo en el registro",Toast.LENGTH_SHORT).show() //Aviso de fallo en el registro
                        Alert.showAlert(this, "Fallo en el registro", Alert.AlertType.ERROR)
                    }
                }
            }
        }

        btnAcceder.setOnClickListener {
            if(email.text.isNotBlank() && passwd.text.isNotBlank()){
                auth.signInWithEmailAndPassword(
                    email.text.toString(),
                    passwd.text.toString()
                ).addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        Log.i("INFO", "Usuario logeado correctamente")
                        Alert.showAlert(this, "Registro exitoso", Alert.AlertType.SUCCESS)
                        showHome(email.text.toString()) //Se ejecuta la funcion de abajo que hace que se vaya al main del medico o del paciente
                        email.text.clear()
                        passwd.text.clear()
                    }else{
                        Alert.showAlert(this, "Fallo en el registro", Alert.AlertType.ERROR)
                    }
                }
            }
        }
    }

    //Si es un medico registrado en la base de datos, va al activity del medico, si no al paciente
    private fun showHome(email: String){//consulta en labase de datos la coleccion de medicos
        //acceder a la base de datos y mirar en la coleccion "Medicos" si existe un documento con el email

        Firebase.firestore.collection("Medicos")
            .document(email).get().addOnSuccessListener {
                if(it.exists()){
                    //startActivity(Intent(this, HomeMedicoActivity::class.java))
                }else{
                    startActivity(Intent(this, HomePaciente::class.java))
                }
            }
    }
}