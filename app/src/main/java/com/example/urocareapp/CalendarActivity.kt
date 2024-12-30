package com.example.urocareapp

import android.os.Bundle
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.urocareapp.R

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Inicialización del CalendarView
        calendarView = findViewById(R.id.calendarView)

        // Listener para el cambio de fecha
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val date = "$dayOfMonth/${month + 1}/$year"
            Toast.makeText(this@CalendarActivity, "Fecha seleccionada: $date", Toast.LENGTH_SHORT).show()
        }
    }
}
