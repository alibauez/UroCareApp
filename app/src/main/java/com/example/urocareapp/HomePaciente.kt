package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class HomePaciente : BaseActivity() {

    private lateinit var btnPantallaInfo: Button
    private lateinit var btnCalendar: Button
    private lateinit var btnCuidados: Button
    private lateinit var btnConsejos: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)
        setSupportActionBar(findViewById(R.id.toolbar))

        btnConsejos = findViewById(R.id.btnConsejos)
        btnPantallaInfo = findViewById(R.id.btnPantallaInfo)
        btnCalendar = findViewById(R.id.btnCalendar)
        btnCuidados = findViewById(R.id.btnCuidados)
        setup()
    }

    fun setup(){
        val intentPantallaInfo = Intent(this, PantallaInfo::class.java)
        val intentCalendar = Intent(this, CalendarActivity::class.java)
        val intentCuidados = Intent(this, CuidadosPostcirujia::class.java)
        val intentConsejos = Intent(this, ConsejosAlimentariosPacienteActivity::class.java)

        btnPantallaInfo.setOnClickListener {
            startActivity(intentPantallaInfo)
        }

        btnCuidados.setOnClickListener {
            startActivity(intentCuidados)
        }

        btnCalendar.setOnClickListener {
            startActivity(intentCalendar)
        }
        btnConsejos.setOnClickListener {
            startActivity(intentConsejos)
        }

    }
}