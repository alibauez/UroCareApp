package com.example.urocareapp.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.R
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ListaPacientes : BaseActivityMedico() {

    private lateinit var recyclerView: RecyclerView
    private val pacientesList = mutableListOf<Paciente>()
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_pacientes)
        setSupportActionBar(findViewById(R.id.toolbar))

        recyclerView = findViewById(R.id.recyclerViewPacientes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PacientesAdapter(pacientesList) { paciente ->
            // Al hacer clic en un paciente, abre la pantalla de gráficas
            val intent = Intent(this, GraficasPaciente::class.java)
            intent.putExtra("pacienteEmail", paciente.email)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Cargar la lista de pacientes desde Firebase
        db.collection("pacientes")
            .get()
            .addOnSuccessListener { result ->
                pacientesList.clear()
                for (document in result) {
                    val nombre = document.getString("nombre") ?: "Sin nombre"
                    val email = document.id
                    val apellidos = document.getString("apellidos") ?: "Sin apellidos"
                    val nombreCompleto = "$nombre $apellidos"
                    pacientesList.add(Paciente(nombreCompleto, email))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar pacientes: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

data class Paciente(val nombreCompleto: String, val email: String)
