package com.example.urocareapp.modelo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.urocareapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AgregarConsejoDialogFragment : DialogFragment() {

    private lateinit var pacienteSpinner: Spinner
    private lateinit var consejosSpinner: Spinner
    private lateinit var nombreEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var guardarButton: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var medicoId: String

    private val pacientesList = mutableListOf<String>()
    private val consejosList = mutableListOf<String>()
    private val consejosMap = mutableMapOf<String, Map<String, String>>() // Para almacenar los datos de los consejos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_agregar_consejo, container, false)

        pacienteSpinner = view.findViewById(R.id.pacienteSpinner)
        consejosSpinner = view.findViewById(R.id.consejosSpinner)
        nombreEditText = view.findViewById(R.id.nombreEditText)
        descripcionEditText = view.findViewById(R.id.descripcionEditText)
        guardarButton = view.findViewById(R.id.guardarButton)

        medicoId = auth.currentUser?.email ?: ""

        obtenerPacientes()
        obtenerConsejosAnteriores()

        consejosSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedConsejo = consejosSpinner.selectedItem.toString()
                if (selectedConsejo != "Seleccionar consejo previamente enviado") {
                    val consejo = consejosMap[selectedConsejo]
                    consejo?.let {
                        nombreEditText.setText(it["titulo"])
                        descripcionEditText.setText(it["descripcion"])
                    }
                } else {
                    // Limpia los campos si el consejo seleccionado es el placeholder
                    nombreEditText.text.clear()
                    descripcionEditText.text.clear()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }

        guardarButton.setOnClickListener {
            guardarConsejo()
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        // Ajustar el tamaño del diálogo
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            (resources.displayMetrics.heightPixels * 0.28).toInt()
        )
    }

    private fun obtenerPacientes() {
        db.collection("pacientes")
            .get()
            .addOnSuccessListener { documents ->
                pacientesList.clear()
                for (document in documents) {
                    pacientesList.add(document.id)  // El ID es el correo del paciente
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pacientesList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                pacienteSpinner.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener pacientes", e)
            }
    }

    private fun obtenerConsejosAnteriores() {
        db.collection("medicos").document(medicoId)
            .collection("consejosEnviados")
            .get()
            .addOnSuccessListener { documents ->
                consejosList.clear()
                consejosMap.clear()

                consejosList.add("Selecciona consejo previo") // Placeholder
                for (document in documents) {
                    val titulo = document.getString("titulo") ?: "Sin título"
                    val descripcion = document.getString("descripcion") ?: ""
                    consejosList.add(titulo)
                    consejosMap[titulo] = mapOf(
                        "titulo" to titulo,
                        "descripcion" to descripcion
                    )
                }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, consejosList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                consejosSpinner.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener consejos", e)
            }
    }

    private fun guardarConsejo() {
        val pacienteCorreo = pacienteSpinner.selectedItem.toString()
        val nombreConsejo = nombreEditText.text.toString().ifBlank { consejosSpinner.selectedItem.toString() }
        val descripcionConsejo = descripcionEditText.text.toString()

        if (pacienteCorreo.isNotEmpty() && nombreConsejo.isNotEmpty() && descripcionConsejo.isNotEmpty()) {
            val consejo = mapOf(
                "titulo" to nombreConsejo,
                "descripcion" to descripcionConsejo
            )

            // Verificar si el consejo ya existe en la colección del paciente
            db.collection("pacientes").document(pacienteCorreo)
                .collection("consejos")
                .whereEqualTo("titulo", nombreConsejo)
                .whereEqualTo("descripcion", descripcionConsejo)
                .get()
                .addOnSuccessListener { pacienteQuery ->
                    if (pacienteQuery.isEmpty) {
                        // El consejo no está en la colección del paciente
                        // Verificar si el consejo ya existe en la colección del médico
                        db.collection("medicos").document(medicoId)
                            .collection("consejosEnviados")
                            .whereEqualTo("titulo", nombreConsejo)
                            .whereEqualTo("descripcion", descripcionConsejo)
                            .get()
                            .addOnSuccessListener { medicoQuery ->
                                if (medicoQuery.isEmpty) {
                                    // Si el consejo no está en la colección del médico, guardarlo
                                    db.collection("medicos").document(medicoId)
                                        .collection("consejosEnviados")
                                        .add(consejo)
                                }

                                // Guardar el consejo en la colección del paciente
                                db.collection("pacientes").document(pacienteCorreo)
                                    .collection("consejos")
                                    .add(consejo)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            requireContext(),
                                            "Consejo asignado exitosamente",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        dismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            requireContext(),
                                            "Error al asignar consejo",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Log.e("Firebase", "Error al agregar consejo al paciente", e)
                                    }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    requireContext(),
                                    "Error al verificar el consejo en la colección del médico",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.e("Firebase", "Error al verificar consejo en el médico", e)
                            }
                    } else {
                        // Si el consejo ya existe en la colección del paciente
                        Toast.makeText(requireContext(), "El paciente ya tiene este consejo", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        requireContext(),
                        "Error al verificar el consejo en la colección del paciente",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("Firebase", "Error al verificar consejo en el paciente", e)
                }
        } else {
            Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }


}
