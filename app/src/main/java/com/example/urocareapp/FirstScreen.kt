package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FirstScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)

        val btnAcceso = findViewById<Button>(R.id.btnAcceso)
        val goToAuthIntent = Intent(this, AuthActivity::class.java)
        // Agrega esta parte para el button3
        val button3 = findViewById<Button>(R.id.button3) // Asegúrate que el ID sea correcto
        val goToTermsConditionsIntent = Intent(this, TermsConditionsActivity::class.java)

        btnAcceso.setOnClickListener {
            startActivity(goToAuthIntent)
        }

        button3.setOnClickListener {
            startActivity(goToTermsConditionsIntent)
        }

    }
}