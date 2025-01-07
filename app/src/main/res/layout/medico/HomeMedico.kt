package com.example.urocareapp.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.urocareapp.CalendarActivity
import com.example.urocareapp.R
import com.example.urocareapp.modelo.AgregarConsejoDialogFragment

class HomeMedico : BaseActivityMedico() {


    private lateinit var addAdvice: Button
    private lateinit var btnHabitos: Button
    private lateinit var btnCalendar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_medico)
        setSupportActionBar(findViewById(R.id.toolbar))


        // Asociar los botones con las vistas del XML
        addAdvice = findViewById(R.id.addAdvice)
        btnHabitos = findViewById(R.id.btnHabitos)
        btnCalendar = findViewById(R.id.btnCalendar)

        // Configurar listeners para los botones
        setupListeners()

        addAdvice.setOnClickListener {
            // Mostrar el DialogFragment para agregar consejo
            val dialog = AgregarConsejoDialogFragment()
            dialog.show(supportFragmentManager, "AgregarConsejoDialog")
        }

    }

    private fun setupListeners() {


        // Abrir Control de Hábitos
        btnHabitos.setOnClickListener {
            // val intent = Intent(this, HabitosSeguimientoActivity::class.java)
            // startActivity(intent)
        }

        // Abrir Calendario Médico
        btnCalendar.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
    }
}
