package com.example.urocareapp

import android.app.Dialog
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
        val alturaTextView = findViewById<TextView>(R.id.etHeight)
        val pesoTextView = findViewById<TextView>(R.id.etWeight)

        val bloodGroupSpinner = findViewById<Spinner>(R.id.spinnerBloodGroup)
        val etHeight = findViewById<EditText>(R.id.etHeight)
        val etWeight = findViewById<EditText>(R.id.etWeight)
        val birthDateTextView = findViewById<TextView>(R.id.tvDate)
        val btnSave = findViewById<Button>(R.id.btnSave)
        val btnChangePassword = findViewById<Button>(R.id.btnChangePassword) // Botón para cambiar contraseña

        val db = Firebase.firestore
        val user1 = Firebase.auth.currentUser
        val userId = user1?.uid
        val btnEdit = findViewById<Button>(R.id.btnEditAllergies)
        val allergies = mutableListOf<String>()
        val btnAñadirAllergies = findViewById<Button>(R.id.btnAñadirAllergies)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAllergies)



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
                var nombreBD = it.get("nombre").toString()
                userNameTextView.setText(if (nombreBD != "null") nombreBD else "")
                var genderBd = it.get("genero").toString()
                userGenderTextView.setText(if (genderBd != "null") genderBd else "")
                var alturaBD = it.get("altura").toString()
                alturaTextView.setText(if (alturaBD != "null") alturaBD else "")
                var pesoBd = it.get("peso").toString()
                pesoTextView.setText(if (pesoBd != "null") pesoBd else "")
                var groupBD = it.get("grupoSanguineo").toString()
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
                allergies.clear()
                allergies.addAll(allergiesList)
                setupRecyclerView(allergiesList)
            }

        btnEdit.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_edit_allergies)

            val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewEditAllergies)
            val btnCloseDialog = dialog.findViewById<Button>(R.id.btnCloseDialog)

            // Declarar adapter aquí, accesible para todos los bloques dentro del diálogo
            val adapter = EditAllergiesAdapter(allergies) { position ->
                // Elimina el ítem de la lista local
                val removedAllergy = allergies.removeAt(position)
                recyclerView.adapter?.notifyItemRemoved(position)



                email?.let {
                    db.collection("pacientes")
                        .document(email)
                        .update("alergias", allergies)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Alergia eliminada", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase", "Error al eliminar alergia: ", e)
                            // Si hay un error, vuelve a agregar la alergia eliminada a la lista local
                            allergies.add(position, removedAllergy)
                            recyclerView.adapter?.notifyItemInserted(position)
                        }
                }
            }

            // Configura el RecyclerView
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

            // Al presionar el botón de cerrar, recargar la actividad actual
            btnCloseDialog.setOnClickListener {
                dialog.dismiss()
                recreate() // Recarga la actividad actual
            }

            dialog.show()
        }

        btnAñadirAllergies.setOnClickListener {
            // Create a dialog for adding a new allergy
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_add_allergy)

            val allergyEditText = dialog.findViewById<EditText>(R.id.etNewAllergy) // EditText to input new allergy
            val btnAddAllergy = dialog.findViewById<Button>(R.id.btnAddAllergy) // Button to confirm the addition
            val btnCloseDialog = dialog.findViewById<Button>(R.id.btnCloseDialog)

            // Add button click listener to add the allergy
            btnAddAllergy.setOnClickListener {
                val newAllergy = allergyEditText.text.toString().trim()
                if (newAllergy.isNotEmpty()) {
                    // Add new allergy to the local list
                    allergies.add(newAllergy)

                    // Notify RecyclerView adapter of the new item
                    recyclerView.adapter?.notifyItemInserted(allergies.size - 1)

                    // Update Firebase with the new list of allergies
                    email?.let {
                        db.collection("pacientes")
                            .document(email)
                            .update("alergias", allergies)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Alergia añadida", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase", "Error al añadir alergia: ", e)
                                // Optionally, handle failure case (like undoing the addition in the local list)
                            }
                    }

                    // Close the dialog after adding the allergy
                    dialog.dismiss()
                    recreate()
                } else {
                    Toast.makeText(this, "Por favor ingresa una alergia", Toast.LENGTH_SHORT).show()
                }
            }

            // Close dialog button
            btnCloseDialog.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }






        // Botón para navegar a la pantalla de registro
        val personalDataButton: Button = findViewById(R.id.btnPersonalData)
        personalDataButton.setOnClickListener {
            val intent = Intent(this, RegistroPaciente::class.java)
            startActivity(intent)
        }




        // Cargar las opciones del Spinner desde el recurso strings.xml
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
            val updatedHeight = alturaTextView.text.toString()
            val updatedWeight = pesoTextView.text.toString()
            val updateBlood = bloodGroupSpinner.selectedItem.toString()

            val userData = hashMapOf(
                "altura" to updatedHeight,
                "peso" to updatedWeight,
                "grupoSanguineo" to updateBlood,
            )

            userId?.let {
                db.collection("pacientes").document(email.toString())
                    .set(userData, SetOptions.merge())
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

    class EditAllergiesAdapter(
        private val allergies: MutableList<String>,
        private val onItemRemoveListener: (Int) -> Unit
    ) : RecyclerView.Adapter<EditAllergiesAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textViewAllergy: TextView = view.findViewById(R.id.tvAllergy)
            val buttonRemove: Button = view.findViewById(R.id.btnRemove)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_allergy_edit, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val allergy = allergies[position]
            holder.textViewAllergy.text = allergy

            holder.buttonRemove.setOnClickListener {
                // Llama al listener para manejar la eliminación
                onItemRemoveListener(position)
            }
        }

        override fun getItemCount(): Int = allergies.size
    }






}
