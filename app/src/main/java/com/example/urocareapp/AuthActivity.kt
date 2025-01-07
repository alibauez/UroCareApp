package com.example.urocareapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
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
            .document(email) // Busca por el ID del documento, que es el email
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Si el documento existe, redirige al Home de Médicos
                    startActivity(Intent(this, HomeMedico::class.java))
                } else {
                    // Si no existe, redirige al Home de Pacientes
                    startActivity(Intent(this, HomePaciente::class.java))
                }
            }
            .addOnFailureListener { exception ->
                // Manejo de errores al consultar Firestore
                Alert.showAlert(this, "Error al verificar el rol: ${exception.message}", Alert.AlertType.ERROR)
            }
    }
}
