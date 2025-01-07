package com.example.urocareapp.medico

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.urocareapp.R

class HomeMedico : AppCompatActivity() {

    private lateinit var btnPantallaInfo: Button
    private lateinit var btnCuidados: Button
    private lateinit var btnConsejos: Button
    private lateinit var btnHabitos: Button
    private lateinit var btnCalendar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_medico)

        // Asociar los botones con las vistas del XML
        btnPantallaInfo = findViewById(R.id.btnPantallaInfo)
        btnCuidados = findViewById(R.id.btnCuidados)
        btnConsejos = findViewById(R.id.btnConsejos)
        btnHabitos = findViewById(R.id.btnHabitos)
        btnCalendar = findViewById(R.id.btnCalendar)

        // Configurar listeners para los botones
        setupListeners()
    }

    private fun setupListeners() {
        // Abrir Pantalla de Información
        btnPantallaInfo.setOnClickListener {
            // val intent = Intent(this, PantallaInfoActivity::class.java)
            // startActivity(intent)
        }

        // Abrir Cuidados Postquirúrgicos
        btnCuidados.setOnClickListener {
            // val intent = Intent(this, CuidadosPostActivity::class.java)
            // startActivity(intent)
        }

        // Abrir Consejos Alimentarios
        btnConsejos.setOnClickListener {
            // Crear un Intent para abrir la pantalla ConsejosAlimentariosMedico
            val intent = Intent(this, ConsejosAlimentariosMedico::class.java)
            startActivity(intent)
        }

        // Abrir Control de Hábitos
        btnHabitos.setOnClickListener {
            // val intent = Intent(this, HabitosSeguimientoActivity::class.java)
            // startActivity(intent)
        }

        // Abrir Calendario Médico
        btnCalendar.setOnClickListener {
            // val intent = Intent(this, CalendarActivity::class.java)
            // startActivity(intent)
        }
    }
}
