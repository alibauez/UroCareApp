package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FirstScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)

        val btnAcceso = findViewById<Button>(R.id.btnAcceso)
        val button3 = findViewById<Button>(R.id.button3) // Asegúrate que el ID sea correcto

        // Intent para ir a la pantalla de autenticación
        val goToAuthIntent = Intent(this, AuthActivity::class.java)

        // Intent para ir a la pantalla de términos y condiciones
        val goToTermsConditionsIntent = Intent(this, TermsConditionsActivity::class.java)
        goToTermsConditionsIntent.putExtra("SOURCE", "LOGIN") // Agregar extra para indicar origen

        // Configurar el evento click del botón de acceso
        btnAcceso.setOnClickListener {
            startActivity(goToAuthIntent)
        }

        // Configurar el evento click del botón de términos y condiciones
        button3.setOnClickListener {
            startActivity(goToTermsConditionsIntent)
        }
    }
}
