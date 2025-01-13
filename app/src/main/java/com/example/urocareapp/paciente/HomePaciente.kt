package com.example.urocareapp.paciente

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.urocareapp.BaseActivity
import com.example.urocareapp.CalendarActivity
import com.example.urocareapp.R
import com.example.urocareapp.SeguimientoDeHabitos
import com.example.urocareapp.chatbot.ChatActivity

class HomePaciente : BaseActivity() {

    private lateinit var btnPantallaInfo: Button
    private lateinit var btnCalendar: Button
    private lateinit var btnCuidados: Button
    private lateinit var btnConsejos: Button
    private lateinit var btnHabitos: Button
    private lateinit var btnChat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)
        setSupportActionBar(findViewById(R.id.toolbar))

        btnConsejos = findViewById(R.id.btnConsejos)
        btnPantallaInfo = findViewById(R.id.btnPantallaInfo)
        btnCalendar = findViewById(R.id.btnCalendar)
        btnCuidados = findViewById(R.id.btnCuidados)
        btnConsejos = findViewById(R.id.btnConsejos)
        btnHabitos = findViewById(R.id.btnHabitos)
        btnChat = findViewById(R.id.btnChat)

        setup()
    }

    fun setup(){
        val intentPantallaInfo = Intent(this, PantallaInfo::class.java)
        val intentCalendar = Intent(this, CalendarActivity::class.java)
        val intentCuidados = Intent(this, CuidadosPostcirujia::class.java)
        val intentConsejos = Intent(this, ConsejosAlimentarios::class.java)
        val intentHabitos = Intent(this, SeguimientoDeHabitos::class.java)

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
        btnHabitos.setOnClickListener {
            startActivity(intentHabitos)
        }
        btnChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
    }
}