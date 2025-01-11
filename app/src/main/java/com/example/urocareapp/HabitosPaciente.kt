package com.example.urocareapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.urocareapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HabitosPaciente : BaseActivity() {


    private lateinit var etWaterLiters: EditText
    private lateinit var etPhysicalActivity: EditText
    private lateinit var switchMedication: Switch
    private lateinit var btnSaveHabits: Button

    private val db = Firebase.firestore
    private val currentUserEmail = Firebase.auth.currentUser?.email
    private val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    private lateinit var tvSwitchText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habitospaciente)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Vincular vistas
        etWaterLiters = findViewById(R.id.etWaterLiters)
        etPhysicalActivity = findViewById(R.id.etPhysicalActivity)
        switchMedication = findViewById(R.id.switchMedication)
        btnSaveHabits = findViewById(R.id.btnSaveHabits)
        tvSwitchText = findViewById(R.id.tvSwitchText)


        // Cargar datos si ya existen para el día actual
        loadExistingHabits()

        // Acción del botón "Guardar o Modificar Hábitos"
        btnSaveHabits.setOnClickListener {
            saveOrUpdateHabits()
        }
        checkAndCreateDailyHabits()

        switchMedication.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tvSwitchText.text = "Sí"
            } else {
                tvSwitchText.text = "No"
            }
        }
    }







    private fun loadExistingHabits() {
        if (currentUserEmail == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("pacientes")
            .document(currentUserEmail)
            .collection("habitos")
            .document(currentDate)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // Cargar datos existentes
                    val waterLiters = document.getDouble("agua") ?: 0.0
                    val physicalActivity = document.getLong("actividadFisica")?.toInt() ?: 0
                    val medication = document.getBoolean("medicacion") ?: false

                    // Rellenar los campos con los datos existentes
                    etWaterLiters.setText(waterLiters.toString())
                    etPhysicalActivity.setText(physicalActivity.toString())
                    switchMedication.isChecked = medication

                    // Cambiar el texto del botón para indicar que se está modificando
//                    btnSaveHabits.text = "Modificar Hábitos"
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar hábitos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveOrUpdateHabits() {
        if (currentUserEmail == null) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar entrada de datos
        val waterLiters = etWaterLiters.text.toString().toFloatOrNull()
        val physicalActivityMinutes = etPhysicalActivity.text.toString().toIntOrNull()
        val medicationTaken = switchMedication.isChecked

        if (waterLiters == null || physicalActivityMinutes == null) {
            Toast.makeText(this, "Por favor, completa todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar rango de agua (0.0 a 10.0)
        if (waterLiters < 0.0 || waterLiters > 10.0) {
            etWaterLiters.error = "El valor debe estar entre 0.0 y 10.0"
            return
        }

        // Validar rango de actividad física (0 a 500)
        if (physicalActivityMinutes < 0 || physicalActivityMinutes > 500) {
            etPhysicalActivity.error = "El valor debe estar entre 0 y 500"
            return
        }

        // Redondear agua a 2 decimales
        val roundedWaterLiters = String.format("%.2f", waterLiters).toDouble()

        // Crear los datos a guardar o modificar
        val habitData = hashMapOf(
            "agua" to roundedWaterLiters,
            "actividadFisica" to physicalActivityMinutes,
            "medicacion" to medicationTaken
        )

        db.collection("pacientes")
            .document(currentUserEmail)
            .collection("habitos")
            .document(currentDate)
            .set(habitData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(this, "Hábitos guardados/modificados correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar hábitos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkAndCreateDailyHabits() {
        val email = Firebase.auth.currentUser?.email ?: return
        val currentDate = getCurrentDate() // "yyyy-MM-dd"

        val db = Firebase.firestore
        val habitsCollection = db.collection("pacientes")
            .document(email)
            .collection("habitos")

        habitsCollection.document(currentDate).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Crear el documento con valores iniciales
                    val initialHabitsData = mapOf(
                        "agua" to 0.0,
                        "actividadFisica" to 0,
                        "medicacion" to false
                    )

                    habitsCollection.document(currentDate)
                        .set(initialHabitsData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Datos de hábitos creados para hoy", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al crear datos de hábitos: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Ya existen datos de hábitos para hoy", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al verificar datos de hábitos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para obtener la fecha actual
    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return dateFormat.format(Date())
    }


}

