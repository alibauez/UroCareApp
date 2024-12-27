package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : AppCompatActivity() {

    protected abstract val currentScreen: Int // Para saber en qué pantalla estamos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(findViewById(R.id.toolbar))


        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    if (currentScreen != R.id.nav_home) {
                        val intent = Intent(this, HomePaciente::class.java)
                        startActivity(intent)
                    }
                    true
                }
                R.id.nav_profile -> {
                    if (currentScreen != R.id.nav_profile) {
                        // Lógica para ir a la pantalla de perfil
                        // Ejemplo: startActivity(Intent(this, ProfileActivity::class.java))
                    }
                    true
                }
                R.id.nav_calendar -> {
                    if (currentScreen != R.id.nav_calendar) {
                        // Lógica para ir a la pantalla de calendario
                        // Ejemplo: startActivity(Intent(this, CalendarActivity::class.java))
                    }
                    true
                }
                R.id.nav_info -> {
                    if (currentScreen != R.id.nav_info) {
                        val intent = Intent(this, PantallaInfo::class.java)
                        startActivity(intent)
                    }
                    true
                }
                else -> false
            }
        }
        // Para que el item seleccionado se quede marcado
        bottomNavigationView?.selectedItemId = currentScreen
    }
}