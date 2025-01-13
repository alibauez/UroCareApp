package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.urocareapp.modelo.Alert
import com.example.urocareapp.paciente.PerfilPaciente
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChangePassActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_pass)
        setSupportActionBar(findViewById(R.id.toolbar))

        val etCurrentPassword = findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = findViewById<EditText>(R.id.etNewPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnUpdatePassword = findViewById<Button>(R.id.btnUpdatePassword)
        val btnSalir2 = findViewById<Button>(R.id.btnSalir2)

        btnUpdatePassword.setOnClickListener {
            val currentPassword = etCurrentPassword.text.toString()
            val newPassword = etNewPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            // Validar que todos los campos están llenos
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Alert.showAlert(this, "Por favor, complete todos los campos", Alert.AlertType.ERROR)
                return@setOnClickListener
            }

            // Validar si las contraseñas no coinciden
            if (newPassword != confirmPassword) {
                Alert.showAlert(this, "Las nuevas contraseñas no coinciden", Alert.AlertType.ERROR)
                return@setOnClickListener
            }

            // Validar si la nueva contraseña es igual a la actual
            if (newPassword == currentPassword) {
                Alert.showAlert(this, "La nueva contraseña debe ser diferente a la actual", Alert.AlertType.ERROR)
                return@setOnClickListener
            }

            val user = Firebase.auth.currentUser
            val email = user?.email

            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, currentPassword)

                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(this, "Contraseña actualizada exitosamente", Toast.LENGTH_SHORT).show()
                                        finish() // Cerrar la actividad
                                    } else {
                                        Alert.showAlert(this, "Error al actualizar la contraseña", Alert.AlertType.ERROR)
                                    }
                                }
                        } else {
                            Alert.showAlert(this, "La contraseña actual es incorrecta", Alert.AlertType.ERROR)
                        }
                    }
            } else {
                Alert.showAlert(this, "Usuario no iniciado sesión", Alert.AlertType.ERROR)
            }
        }

        btnSalir2.setOnClickListener {
            val intent = Intent(this, PerfilPaciente::class.java)
            startActivity(intent)
            finish()
        }
    }
}
