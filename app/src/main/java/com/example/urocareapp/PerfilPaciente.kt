package com.example.urocareapp

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
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
        val db = Firebase.firestore
        val user1 = Firebase.auth.currentUser
        val userId = user1?.uid
        val btnEdit = findViewById<Button>(R.id.btnEdit)
        val allergies = mutableListOf<String>()



        val email = Firebase.auth.currentUser?.email
        val birthDateTextView = findViewById<TextView>(R.id.tvDate)



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
                    val position = bloodGroupOptions.indexOf(groupBD) // Encuentra la posición del valor en las opciones
                    if (position >= 0) {
                        bloodGroupSpinner.setSelection(position) // Establece el valor inicial del Spinner
                    }
                }
                // Fecha de nacimiento
                val birthDateTimestamp = it.getTimestamp("fechaNacimiento")
                birthDateTimestamp?.let { timestamp ->
                    val birthDate = timestamp.toDate()
                    val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                    val formattedDate = dateFormat.format(birthDate)

                    birthDateTextView.text = formattedDate
                }
                val allergiesList = it.get("alergias") as? List<String> ?: emptyList()
                Log.d("alergias", allergiesList.toString())
                setupRecyclerView(allergiesList)
            }

        btnEdit.setOnClickListener {
            val dialog = Dialog(this)
            dialog.setContentView(R.layout.dialog_edit_allergies)

            val recyclerView = dialog.findViewById<RecyclerView>(R.id.recyclerViewEditAllergies)
            val btnCloseDialog = dialog.findViewById<Button>(R.id.btnCloseDialog)

            val adapter = EditAllergiesAdapter(allergies) { position ->
                allergies.removeAt(position)
                adapter.notifyItemRemoved(position)
            }

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter

            btnCloseDialog.setOnClickListener {
                dialog.dismiss()
            }

            dialog.show()
        }





        // Cargar las opciones del Spinner desde el recurso strings.xml
        val bloodGroupOptions = resources.getStringArray(R.array.blood_group_options)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroupOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner.adapter = adapter



        bloodGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedGroup = bloodGroupOptions[position]
                // Guardar en Firebase o en tu base de datos

                val profileUpdates = userProfileChangeRequest {
                    displayName = selectedGroup // Solo como ejemplo, ajusta esto según tu estructura
                }
                user1?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("Firebase", "Grupo sanguíneo actualizado: $selectedGroup")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Acción cuando no se selecciona nada (opcional)
            }
        }


        val btnSave = findViewById<Button>(R.id.btnSave)
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
    fun setupRecyclerView(allergies: List<String>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAllergies)
        recyclerView.layoutManager = LinearLayoutManager(this) // Asegúrate de configurar el layout manager
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
                onItemRemoveListener(position)
            }
        }

        override fun getItemCount(): Int = allergies.size
    }





}