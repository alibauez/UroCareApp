package com.example.urocareapp.modelo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.urocareapp.R
import com.google.firebase.firestore.FirebaseFirestore

class AgregarConsejoDialogFragment : DialogFragment() {

    private lateinit var pacienteSpinner: Spinner
    private lateinit var nombreEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var guardarButton: Button
    private lateinit var pacientesList: List<String> // Lista de correos de los pacientes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_agregar_consejo, container, false)

        pacienteSpinner = view.findViewById(R.id.pacienteSpinner)
        nombreEditText = view.findViewById(R.id.nombreEditText)
        descripcionEditText = view.findViewById(R.id.descripcionEditText)
        guardarButton = view.findViewById(R.id.guardarButton)

        pacientesList = mutableListOf()

        obtenerPacientes()

        guardarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val pacienteCorreo = pacienteSpinner.selectedItem.toString()

            if (nombre.isNotEmpty() && descripcion.isNotEmpty() && pacienteCorreo.isNotEmpty()) {
                guardarConsejo(pacienteCorreo, nombre, descripcion)
            } else {
                Toast.makeText(requireContext(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun obtenerPacientes() {
        val db = FirebaseFirestore.getInstance()

        // Asegúrate de que pacientesList sea mutable
        val pacientesList = mutableListOf<String>()  // Inicialización explícita de la lista mutable

        // Obtener la lista de pacientes (cuyos documentos tienen el correo como ID)
        db.collection("pacientes")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    pacientesList.add(document.id)  // El ID es el correo del paciente
                }
                // Configurar el Spinner con los correos de los pacientes
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, pacientesList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                pacienteSpinner.adapter = adapter
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error al obtener pacientes", e)
            }
    }


    private fun guardarConsejo(pacienteCorreo: String, nombre: String, descripcion: String) {
        val db = FirebaseFirestore.getInstance()

        // Crear el consejo
        val consejo = mapOf(
            "titulo" to nombre,
            "descripcion" to descripcion
        )

        // Obtener referencia al documento del paciente
        val pacienteRef = db.collection("pacientes").document(pacienteCorreo)

        // Guardar el consejo en la subcolección "consejos" del paciente
        pacienteRef.collection("consejos")
            .add(consejo)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Consejo asignado exitosamente", Toast.LENGTH_SHORT).show()
                dismiss()  // Cerrar el diálogo
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al asignar consejo", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error al agregar consejo", e)
            }
    }
}