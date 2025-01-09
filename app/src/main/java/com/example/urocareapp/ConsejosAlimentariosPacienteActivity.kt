package com.example.urocareapp

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.modelo.Consejo
import com.example.urocareapp.modelo.ConsejosAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ConsejosAlimentariosPacienteActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConsejosAdapter
    private lateinit var tituloTextView: TextView
    private val consejosList = mutableListOf<Consejo>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consejos_alimentarios_paciente)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Inicializar vistas
        recyclerView = findViewById(R.id.consejosRecyclerView)
        tituloTextView = findViewById(R.id.tituloTextView)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ConsejosAdapter(consejosList) { consejo ->
            eliminarConsejo(consejo) // Callback para eliminar el consejo
        }
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

                consejosList.clear()
                consejosList.addAll(nuevosConsejos)
                adapter.notifyDataSetChanged()

                // Cambiar texto dependiendo de si hay consejos o no
                if (consejosList.isEmpty()) {
                    tituloTextView.text = "No hay consejos disponibles"
                } else {
                    tituloTextView.text = getString(R.string.listado_de_consejos_alimentarios)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar los consejos", Toast.LENGTH_SHORT).show()
                Log.e("Firebase", "Error al cargar los consejos", e)
                e.printStackTrace()
            }
    }

    private fun eliminarConsejo(consejo: Consejo) {
        val db = FirebaseFirestore.getInstance()
        val pacienteCorreo = obtenerCorreoPacienteActual()

        db.collection("pacientes")
            .document(pacienteCorreo)
            .collection("consejos")
            .whereEqualTo("titulo", consejo.titulo)
            .whereEqualTo("descripcion", consejo.descripcion)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    document.reference.delete()
                        .addOnSuccessListener {
                            Toast.makeText(this, "Consejo eliminado", Toast.LENGTH_SHORT).show()
                            cargarConsejos() // Recargar los consejos después de eliminar
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al eliminar el consejo", Toast.LENGTH_SHORT).show()
                            Log.e("Firebase", "Error al eliminar el consejo", e)
                        }
                }
            }
    }

    private fun obtenerCorreoPacienteActual(): String {
        val usuarioActual = FirebaseAuth.getInstance().currentUser
        return usuarioActual?.email ?: ""
    }
}
