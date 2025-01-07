package com.example.urocareapp.medico

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.example.urocareapp.R

class ConsejosAlimentariosMedico : BaseActivityMedico() {

    private lateinit var etHidratacion: EditText
    private lateinit var btnGuardarHidratacion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consejos_alimentarios_medico)

        // Inicializar Firebase
        val database = FirebaseDatabase.getInstance()
        val userRef = database.getReference("usuarios").child("usuario_1") // Cambia por el ID del usuario actual

        // Enlazar vistas
        etHidratacion = findViewById(R.id.etHidratacion)
        btnGuardarHidratacion = findViewById(R.id.btnGuardarHidratacion)

        // Guardar datos de Hidratación
        btnGuardarHidratacion.setOnClickListener {
            val hidratacion = etHidratacion.text.toString()
            if (hidratacion.isNotEmpty()) {
                userRef.child("hidratacion").setValue(hidratacion)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al guardar los datos", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor, introduce tus datos de hidratación", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
