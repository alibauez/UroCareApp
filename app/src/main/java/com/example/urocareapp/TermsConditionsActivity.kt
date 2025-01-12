package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TermsConditionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_conditions)

        val buttonTerms = findViewById<Button>(R.id.buttonTerms)

        // Obtener el valor del extra para determinar la fuente
        val source = intent.getStringExtra("SOURCE") ?: "LOGIN"

        // Configurar el Intent de redirección basado en la fuente
        buttonTerms.setOnClickListener {
            val nextIntent = when (source) {
                "REGISTER" -> Intent(this, RegistroPaciente::class.java)
                else -> Intent(this, AuthActivity::class.java)
            }
            startActivity(nextIntent)
            finish() // Finaliza la actividad actual
        }
    }
}
