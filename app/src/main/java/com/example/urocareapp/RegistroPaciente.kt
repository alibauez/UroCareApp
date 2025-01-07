package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.urocareapp.medico.HomeMedico
import com.example.urocareapp.medico.PerfilMedico
import com.example.urocareapp.modelo.Alert
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class RegistroPaciente : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val email = Firebase.auth.currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        setSupportActionBar(findViewById(R.id.toolbar))

        val nameEditText: EditText = findViewById(R.id.etName)
        val surnameEditText: EditText = findViewById(R.id.etSurname)
        val dobEditText: EditText = findViewById(R.id.etDob)
        val genderSpinner: Spinner = findViewById(R.id.spinnerGender)

        val continueButton: Button = findViewById(R.id.btnContinue)

        // Configurar Spinner de Género
        val genderOptions = listOf("Masculino", "Femenino", "Otro")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter



        db.collection("pacientes").document(email.toString()).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    nameEditText.setText(document.getString("nombre"))
                    surnameEditText.setText(document.getString("apellidos"))
                    // Convertir Timestamp a String
                    val dobTimestamp = document.getTimestamp("fechaNacimiento")
                    if (dobTimestamp != null) {
                        val date = dobTimestamp.toDate()
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        dobEditText.setText(dateFormat.format(date))
                    }
                    val gender = document.getString("genero")
                    val bloodGroup = document.getString("grupoSanguineo")

                    if (gender != null) {
                        val genderIndex = genderOptions.indexOf(gender)
                        if (genderIndex >= 0) genderSpinner.setSelection(genderIndex)
                    }


                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
            }


        continueButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val surname = surnameEditText.text.toString().trim()
            val dob = dobEditText.text.toString().trim()
            val gender = genderSpinner.selectedItem.toString()


            if (name.isEmpty() || surname.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos obligatorios.", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    // Parsear la fecha de nacimiento a un objeto Date
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dobDate = dateFormat.parse(dob)

                    // Convertir Date a Timestamp
                    val dobTimestamp = dobDate?.let { Timestamp(it) }

                    val userData = mutableMapOf<String, Any?>(
                        "nombre" to name,
                        "apellidos" to surname,
                        "fechaNacimiento" to dobTimestamp,
                        "genero" to gender,

                    )

                    db.collection("medicos") // Verifica que estés usando el nombre correcto "medicos"
                        .document(email.toString()) // Busca por el ID del documento, que es el email
                        .get()
                        .addOnSuccessListener { document ->
                            if (document.exists()) {
                                db.collection("medicos").document(email.toString())
                                    .update(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Datos guardados exitosamente.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Redirigir a la pantalla de perfil
                                        val intent = Intent(this, PerfilMedico::class.java)
                                        startActivity(intent)

                                        // Finaliza la actividad actual para que no quede en el stack
                                        finish()
                                    }
                            } else {
                                db.collection("pacientes").document(email.toString())
                                    .update(userData).addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Datos guardados exitosamente.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        // Redirigir a la pantalla de perfil
                                        val intent = Intent(this, PerfilPaciente::class.java)
                                        startActivity(intent)

                                        // Finaliza la actividad actual para que no quede en el stack
                                        finish()
                                    }

                            }
                        }


                } catch (e: Exception) {
                    Toast.makeText(this, "Formato de fecha inválido. Usa dd/MM/yyyy.", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }


}
