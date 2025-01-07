package com.example.urocareapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.urocareapp.modelo.Alert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AuthActivity : AppCompatActivity() {

    private lateinit var btnRegistro: Button
    private lateinit var email: EditText
    private lateinit var passwd: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var btnAcceder: Button
    private lateinit var checkBoxRememberMe: CheckBox // Para el CheckBox

    // Preferencias para almacenar el estado de "Recordar sesión"
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        btnRegistro = findViewById(R.id.btnRegistro)
        email = findViewById(R.id.emailEditText)
        passwd = findViewById(R.id.passwdEditText)
        auth = Firebase.auth
        btnAcceder = findViewById(R.id.btnAcceder)
        checkBoxRememberMe = findViewById(R.id.checkBoxRememberMe)

        // Inicializa SharedPreferences
        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        // Aquí no hacemos login automáticamente, solo verificamos si las credenciales están guardadas
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)

        // Si las credenciales están guardadas y el CheckBox está marcado, cargarlas
        if (savedEmail != null && savedPassword != null) {
            email.setText(savedEmail)
            passwd.setText(savedPassword)
            checkBoxRememberMe.isChecked = true // Marcar la casilla si hay credenciales guardadas
        } else {
            checkBoxRememberMe.isChecked = false // Desmarcar la casilla si no hay credenciales guardadas
        }

        // Al cargar la actividad, establecer el color del CheckBox según si está marcado o no
        setCheckBoxColor(checkBoxRememberMe.isChecked)

        checkBoxRememberMe.setOnCheckedChangeListener { _, isChecked ->
            setCheckBoxColor(isChecked)

            if (!isChecked) {
                // Si el checkbox está desmarcado, eliminar las credenciales
                with(sharedPreferences.edit()) {
                    remove("email")
                    remove("password")
                    apply()
                }
            }
        }

        setup()
    }

    private fun setCheckBoxColor(isChecked: Boolean) {
        if (isChecked) {
            // Color cuando está marcado
            checkBoxRememberMe.buttonTintList = ContextCompat.getColorStateList(this, R.color.primary)
        } else {
            // Color cuando NO está marcado
            checkBoxRememberMe.buttonTintList = ContextCompat.getColorStateList(this, R.color.black)
        }
    }

    fun setup() {
        val btnResetPass = findViewById<Button>(R.id.btnResetPassword)
        val resetPasswordIntent = Intent(this, ResetPassActivity::class.java)

        btnRegistro.setOnClickListener {
            if (email.text.isNotBlank() && passwd.text.isNotBlank()) {
                auth.createUserWithEmailAndPassword(
                    email.text.toString(),
                    passwd.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showHome(email.text.toString())
                        email.text.clear()
                        passwd.text.clear()
                    } else {
                        Alert.showAlert(this, "Registro fallido", Alert.AlertType.ERROR)
                    }
                }
            }
        }

        btnAcceder.setOnClickListener {
            if (email.text.isNotBlank() && passwd.text.isNotBlank()) {
                auth.signInWithEmailAndPassword(
                    email.text.toString(),
                    passwd.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Guardar las credenciales si el CheckBox está marcado
                        if (checkBoxRememberMe.isChecked) {
                            with(sharedPreferences.edit()) {
                                putString("email", email.text.toString())
                                putString("password", passwd.text.toString())
                                apply()
                            }
                        }
                        startActivity(Intent(this, HomePaciente::class.java))
                        email.text.clear()
                        passwd.text.clear()
                    } else {
                        Alert.showAlert(this, "Fallo en el inicio de sesión", Alert.AlertType.ERROR)
                    }
                }
            }
        }

        btnResetPass.setOnClickListener {
            startActivity(resetPasswordIntent)
        }
    }

    private fun showHome(email: String) {
        Firebase.firestore.collection("Medicos")
            .document(email).get().addOnSuccessListener {
                if (it.exists()) {
                    //startActivity(Intent(this, HomeMedicoActivity::class.java))
                } else {
                    //startActivity(Intent(this, HomePaciente::class.java))
                }
            }
    }
}
