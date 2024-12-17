package com.example.urocareapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class PantallaInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantallainfo)

//        // Referencia al botón "Siguiente"
//        val buttonNext: Button = findViewById(R.id.buttonNext)
//
//        // Acción al pulsar el botón
//        buttonNext.setOnClickListener {
//            Toast.makeText(this, "Siguiente pulsado", Toast.LENGTH_SHORT).show()
//        }
    }
}
