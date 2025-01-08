package com.example.urocareapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.modelo.Consejo
import com.example.urocareapp.modelo.ConsejosAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConsejosAlimentariosPacienteActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConsejosAdapter
    private val consejosList = mutableListOf<Consejo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consejos_alimentarios_paciente)

        recyclerView = findViewById(R.id.consejosRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ConsejosAdapter(consejosList)
        recyclerView.adapter = adapter

        cargarConsejos()
    }

    private fun cargarConsejos() {
        val db = FirebaseFirestore.getInstance()
        val pacienteCorreo = obtenerCorreoPacienteActual() // Implementar esta función para obtener el correo del paciente

        db.collection("pacientes")
            .document(pacienteCorreo)
            .collection("consejos")
            .get()
            .addOnSuccessListener { documents ->
                val nuevosConsejos = documents.mapNotNull { document ->
                    document.toObject(Consejo::class.java)
                }
                adapter.actualizarConsejos(nuevosConsejos)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los consejos", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error al cargar los consejos", e)
                e.printStackTrace() // Agregado para ver el error en el log
            }
    }

    private fun obtenerCorreoPacienteActual(): String {
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        return usuarioActual?.email ?: ""
    }
}
