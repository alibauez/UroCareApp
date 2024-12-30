package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

abstract class BaseActivity : AppCompatActivity() {





    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.item_home -> {
                startActivity(Intent(this, HomePaciente::class.java))
                true
            }
            R.id.item_salir -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                true
            }
            R.id.item_perfil -> {
                startActivity(Intent(this, PerfilPaciente::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}