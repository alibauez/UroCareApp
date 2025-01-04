package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        val userGenderTextView = findViewById<TextView>(R.id.tvGender)
        val bloodGroupSpinner = findViewById<Spinner>(R.id.spinnerBloodGroup)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val birthDateTextView = findViewById<TextView>(R.id.tvDate)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword) // Botón para cambiar contraseña

        val db = Firebase.firestore
        val user1 = Firebase.auth.currentUser
        val userId = user1?.uid
        val email = Firebase.auth.currentUser?.email

        // Configurar botón para cambiar contraseña
        btnChangePassword.setOnClickListener {
            val intent = Intent(this, ChangePassActivity::class.java)
            startActivity(intent)
        }

        // Cargar datos del usuario
        Firebase.firestore.collection("pacientes")
            .document(email.toString())
            .get()
            .addOnSuccessListener {
                val nombreBD = it.get("nombre").toString()
                userNameTextView.text = if (nombreBD != "null") nombreBD else ""
                val genderBd = it.get("genero").toString()
                userGenderTextView.text = if (genderBd != "null") genderBd else ""

                val groupBD = it.get("grupoSanguineo").toString()
                if (groupBD != "null") {
                    val bloodGroupOptions = resources.getStringArray(R.array.blood_group_options)
                    val position = bloodGroupOptions.indexOf(groupBD)
                    if (position >= 0) {
                        bloodGroupSpinner.setSelection(position)
                    }
                }

                val birthDateTimestamp = it.getTimestamp("fechaNacimiento")
                birthDateTimestamp?.let { timestamp ->
                    val birthDate = timestamp.toDate()
                    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    birthDateTextView.text = dateFormat.format(birthDate)
                }

                val allergiesList = it.get("alergias") as? List<String> ?: emptyList()
                Log.d("alergias", allergiesList.toString())
                setupRecyclerView(allergiesList)
            }

        // Configurar Spinner de grupo sanguíneo
        val bloodGroupOptions = resources.getStringArray(R.array.blood_group_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroupOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner.adapter = adapter

        bloodGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGroup = bloodGroupOptions[position]
                val profileUpdates = userProfileChangeRequest {
                    displayName = selectedGroup
                }
                user1?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "Grupo sanguíneo actualizado: $selectedGroup")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Guardar altura y peso
        btnSave.setOnClickListener {
            val updatedHeight = etHeight.text.toString()
            val updatedWeight = etWeight.text.toString()

            val userData = hashMapOf(
                "height" to updatedHeight,
                "weight" to updatedWeight
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

    class AllergiesAdapter(private val allergies: List<String>) :
        RecyclerView.Adapter<AllergiesAdapter.AllergyViewHolder>() {

        inner class AllergyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val allergyTextView: TextView = itemView.findViewById(R.id.tvAllergy)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_allergy, parent, false)
            return AllergyViewHolder(view)
        }

        override fun onBindViewHolder(holder: AllergyViewHolder, position: Int) {
            holder.allergyTextView.text = allergies[position]
        }

        override fun getItemCount(): Int = allergies.size
    }

    private fun setupRecyclerView(allergies: List<String>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAllergies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = AllergiesAdapter(allergies)
    }
}
