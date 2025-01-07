package com.example.urocareapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.urocareapp.medico.HomeMedico
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
    private lateinit var checkBoxRememberMe: CheckBox

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

        sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)

        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)

        if (savedEmail != null && savedPassword != null) {
            email.setText(savedEmail)
            passwd.setText(savedPassword)
            checkBoxRememberMe.isChecked = true
        } else {
            checkBoxRememberMe.isChecked = false
        }

        setCheckBoxColor(checkBoxRememberMe.isChecked)

        checkBoxRememberMe.setOnCheckedChangeListener { _, isChecked ->
            setCheckBoxColor(isChecked)
            if (!isChecked) {
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
            checkBoxRememberMe.buttonTintList = ContextCompat.getColorStateList(this, R.color.primary)
        } else {
            checkBoxRememberMe.buttonTintList = ContextCompat.getColorStateList(this, R.color.black)
        }
    }

    private fun setup() {
        val btnResetPass = findViewById<Button>(R.id.btnResetPassword)
        val resetPasswordIntent = Intent(this, ResetPassActivity::class.java)
        val db = Firebase.firestore
        btnRegistro.setOnClickListener {
            if (email.text.isNotBlank() && passwd.text.isNotBlank()) {
                // Registra al usuario en Firebase Authentication
                auth.createUserWithEmailAndPassword(
                    email.text.toString(),
                    passwd.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Datos iniciales del usuario
                        val userData = hashMapOf(
                            "alergias" to arrayListOf<String>(), // Inicialmente vacío
                            "altura" to "",
                            "peso" to "",
                            "apellidos" to "",
                            "genero" to "",
                            "nombre" to "",
                            "grupoSanguineo" to "0+",
                            "fechaNacimiento" to null
                        )

                        // Crea el documento en Firestore bajo la colección "pacientes"
                        db.collection("pacientes")
                            .document(email.text.toString())
                            .set(userData)
                            .addOnSuccessListener {
                                // Redirige a la pantalla de registro
                                val intent = Intent(this, RegistroPaciente::class.java)
                                startActivity(intent)

                                // Limpia los campos de texto
                                email.text.clear()
                                passwd.text.clear()
                            }
                            .addOnFailureListener { e ->
                                Alert.showAlert(this, "Error al guardar los datos: ${e.message}", Alert.AlertType.ERROR)
                            }
                    } else {
                        Alert.showAlert(this, "Registro fallido: ${task.exception?.message}", Alert.AlertType.ERROR)
                    }
                }
            } else {
                Alert.showAlert(this, "Por favor, completa todos los campos", Alert.AlertType.INFO)
            }
        }



        btnAcceder.setOnClickListener {
            if (email.text.isNotBlank() && passwd.text.isNotBlank()) {
                auth.signInWithEmailAndPassword(
                    email.text.toString(),
                    passwd.text.toString()
                ).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (checkBoxRememberMe.isChecked) {
                            with(sharedPreferences.edit()) {
                                putString("email", email.text.toString())
                                putString("password", passwd.text.toString())
                                apply()
                            }
                        }
                        showHome(email.text.toString())
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
        val db = Firebase.firestore

        db.collection("medicos") // Verifica que estés usando el nombre correcto "medicos"
            .document(email.toString()) // Busca por el ID del documento, que es el email
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nombre = document.getString("nombre")
                    val apellidos = document.getString("apellidos")

                    if (nombre.isNullOrEmpty() || apellidos.isNullOrEmpty()) {
                        // Si "nombre" o "apellidos" están vacíos, redirige a la pantalla de registro
                        Toast.makeText(this, "Por favor completa tu registro.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, RegistroPaciente::class.java))
                    } else {
                        // Si los datos están completos, redirige al Home de Médicos
                        startActivity(Intent(this, HomeMedico::class.java))
                    }
                } else {
                    // Si no existe el documento en "medicos", redirige al Home de Pacientes
                    startActivity(Intent(this, HomePaciente::class.java))
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores al consultar Firestore
                Alert.showAlert(this, "Error al verificar el rol: ${exception.message}", Alert.AlertType.ERROR)
            }

    }
}
