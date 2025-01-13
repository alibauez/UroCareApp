package com.example.urocareapp.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.example.urocareapp.ChangePassActivity
import com.example.urocareapp.R
import com.example.urocareapp.paciente.RegistroPaciente
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PerfilMedico : BaseActivityMedico() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.perfil_medico)
        setSupportActionBar(findViewById(R.id.toolbar))
        val userGenderTextView = findViewById<TextView>(R.id.tvGender)
        val userNameTextView = findViewById<TextView>(R.id.tvName)
        val birthDateTextView = findViewById<TextView>(R.id.tvDate)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword)
        val email = Firebase.auth.currentUser?.email

        // Configurar botón para cambiar contraseña
        btnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePassActivityMedico::class.java)
            startActivity(intent)
        }

        Firebase.firestore.collection("medicos")
            .document(email.toString())
            .get()
            .addOnSuccessListener {
                val nombreBD = it.get("nombre")?.toString() ?: ""
                val apellidoBD = it.get("apellidos")?.toString() ?: ""

                // Concatenar nombre y apellido
                val nombreCompleto = if (nombreBD.isNotEmpty() || apellidoBD.isNotEmpty()) {
                    "$nombreBD $apellidoBD".trim()
                } else {
                    "Nombre no disponible"
                }

                userNameTextView.text = nombreCompleto

                // Asignar género
                val genderBd = it.get("genero")?.toString() ?: ""
                userGenderTextView.text = if (genderBd.isNotEmpty()) genderBd else "Género no disponible"

                // Formatear y asignar fecha de nacimiento
                val birthDateTimestamp = it.getTimestamp("fechaNacimiento")
                birthDateTimestamp?.let { timestamp ->
                    val birthDate = timestamp.toDate()
                    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    birthDateTextView.text = dateFormat.format(birthDate)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar datos del usuario", Toast.LENGTH_SHORT).show()
            }

        // Botón para navegar a la pantalla de registro
        val personalDataButton: Button = findViewById(R.id.btnPersonalData)
        personalDataButton.setOnClickListener {
            val intent = Intent(this, RegistroPaciente::class.java)
            startActivity(intent)
        }
    }
}