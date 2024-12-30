package com.example.urocareapp

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PerfilPaciente : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        setSupportActionBar(findViewById(R.id.toolbar))


        val userNameTextView = findViewById<TextView>(R.id.tvName)
        val userAgeTextView = findViewById<TextView>(R.id.tvAge)
        val userGenderTextView = findViewById<TextView>(R.id.tvGender)

        val bloodGroupSpinner = findViewById<Spinner>(R.id.spinnerBloodGroup)
        val db = Firebase.firestore
        val user1 = Firebase.auth.currentUser
        val userId = user1?.uid

        val etHeight = findViewById<EditText>(R.id.etHeight)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val etAllergies = findViewById<EditText>(R.id.etAllergies)
        val email = Firebase.auth.currentUser?.email



        Firebase.firestore.collection("Pacientes")
            .document(email.toString())
            .get()
            .addOnSuccessListener {
                var nombreBD = it.get("nombre")
                    .toString()//el campo debe ser igual al que se usa en la base de datos (mapOf)
                userNameTextView.setText(if (nombreBD != "null") nombreBD else "")
                var ageBD = it.get("edad").toString()
                userAgeTextView.setText(if (ageBD != "null") ageBD else "")
                var genderBd = it.get("genero").toString()
                userGenderTextView.setText(if (genderBd != "null") genderBd else "")
            }



        // Cargar las opciones del Spinner desde el recurso strings.xml
        val bloodGroupOptions = resources.getStringArray(R.array.blood_group_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroupOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner.adapter = adapter

        // Establecer el valor inicial (opcional, basado en Firebase)
        val user = Firebase.auth.currentUser
        val userBloodGroup = "0+" // Cambiar esto para cargar desde la base de datos o un campo específico
        val initialPosition = bloodGroupOptions.indexOf(userBloodGroup)
        if (initialPosition >= 0) {
            bloodGroupSpinner.setSelection(initialPosition)
        }

        // Obtener el valor seleccionado cuando sea necesario
        val selectedBloodGroup = bloodGroupSpinner.selectedItem.toString()

        bloodGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGroup = bloodGroupOptions[position]
                // Guardar en Firebase o en tu base de datos
                val user = Firebase.auth.currentUser
                val profileUpdates = userProfileChangeRequest {
                    displayName = selectedGroup // Solo como ejemplo, ajusta esto según tu estructura
                }
                user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "Grupo sanguíneo actualizado: $selectedGroup")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Acción cuando no se selecciona nada (opcional)
            }
        }

        // Cargar los datos del usuario desde Firestore
        userId?.let {
            db.collection("users").document(it).get().addOnSuccessListener { document ->
                if (document != null) {
                    // Cargar los datos en los campos
                    etHeight.setText(document.getString("height") ?: "")
                    etWeight.setText(document.getString("weight") ?: "")
                    etAllergies.setText(document.getString("allergies") ?: "")
                } else {
                    Log.d("Firebase", "No se encontró el documento del usuario")
                }
            }.addOnFailureListener { exception ->
                Log.w("Firebase", "Error al obtener datos: ", exception)
            }
        }
        val btnSave = findViewById<Button>(R.id.btnSave)
        btnSave.setOnClickListener {
            val updatedHeight = etHeight.text.toString()
            val updatedWeight = etWeight.text.toString()
            val updatedAllergies = etAllergies.text.toString()

            val userData = hashMapOf(
                "height" to updatedHeight,
                "weight" to updatedWeight,
                "allergies" to updatedAllergies
            )

            userId?.let {
                db.collection("users").document(it).set(userData, SetOptions.merge())
                    .addOnSuccessListener {
                        Log.d("Firebase", "Datos actualizados correctamente")
                        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firebase", "Error al guardar datos: ", e)
                        Toast.makeText(this, "Error al guardar datos", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }




}