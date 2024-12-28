package com.example.urocareapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.urocareapp.modelo.Alert
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ResetPassActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var editTextEmail: EditText
    private lateinit var buttonResetPassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_pass)

        auth = Firebase.auth
        editTextEmail = findViewById(R.id.editTextEmail)
        buttonResetPassword = findViewById(R.id.buttonResetPassword)

        buttonResetPassword.setOnClickListener {
            val email = editTextEmail.text.toString()

            if (email.isNotBlank()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("ResetPasswordActivity", "Email sent.")
                            Alert.showAlert(this, "Se envió un correo electrónico para restablecer la contraseña", Alert.AlertType.SUCCESS)
                            finish() // Cierra la actividad después de enviar el correo
                        } else {
                            Log.w("ResetPasswordActivity", "sendEmailVerification", task.exception)
                            Alert.showAlert(this, "Error al enviar el correo electrónico", Alert.AlertType.ERROR)
                        }
                    }
            } else {
                Alert.showAlert(this, "Ingresa tu correo electrónico", Alert.AlertType.ERROR)
            }
        }
    }
}