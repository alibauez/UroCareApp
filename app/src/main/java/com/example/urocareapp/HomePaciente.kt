package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button

class HomePaciente : BaseActivity() {

    private lateinit var btnPantallaInfo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)
        btnPantallaInfo = findViewById(R.id.btnPantallaInfo) // Inicializa aquí
        setup()
    }

    fun setup(){
        val intentPantallaInfo = Intent(this, PantallaInfo::class.java)
        btnPantallaInfo.setOnClickListener {
            startActivity(intentPantallaInfo)
        }
    }
}