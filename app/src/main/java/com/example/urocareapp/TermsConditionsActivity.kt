package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class TermsConditionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_conditions)

        val buttonTerms = findViewById<Button>(R.id.buttonTerms) // Asegúrate de que el ID sea correcto
        val goToFirstScreenIntent = Intent(this, FirstScreen::class.java)

        buttonTerms.setOnClickListener {
            startActivity(goToFirstScreenIntent)
        }
    }
}