package com.example.urocareapp.medico

import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.urocareapp.AuthActivity
import com.example.urocareapp.HomePaciente
import com.example.urocareapp.PerfilPaciente
import com.example.urocareapp.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

open class BaseActivityMedico : AppCompatActivity() {
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection.
        return when (item.itemId) {
            R.id.item_home -> {
                startActivity(Intent(this, HomeMedico::class.java))
                true
            }
            R.id.item_salir -> {
                Firebase.auth.signOut()
                startActivity(Intent(this, AuthActivity::class.java))
                true
            }
//            R.id.item_perfil -> {
//                startActivity(Intent(this, PerfilPaciente::class.java))
//                true
//            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}