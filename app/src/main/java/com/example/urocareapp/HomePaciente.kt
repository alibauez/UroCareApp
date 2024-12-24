package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class HomePaciente : AppCompatActivity() {
    private lateinit var btnPantallaInfo: Button
//    private lateinit var btnCitas: Button
//    private lateinit var btnPruebas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)
//        setSupportActionBar(findViewById(R.id.toolbar))




//        btnPantallaInfo = findViewById(R.id.btnPantallaInfo)
//        btnPantallaInfo.setOnClickListener{
//            setContentView(R.layout.activity_pantallainfo)
//        }
        /*
        btnCitas = findViewById(R.id.btnCitas)
        btnCitas.setOnClickListener {
            Log.i("INFO", "Se ha pulsado el botón")

        }
        btnPruebas = findViewById(R.id.btnPruebas)
        btnPruebas.setOnClickListener {
            Toast.makeText(this, "Aún no hay activity", Toast.LENGTH_SHORT).show()

        }
        */
    }
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        val inflater: MenuInflater = menuInflater
//        inflater.inflate(R.menu.menu_paciente, menu)
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle item selection.
//        return when (item.itemId) {
//            R.id.item_home -> {
//                startActivity(Intent(this, HomePaciente::class.java))
//                true
//            }
//            R.id.item_salir -> {
//                Firebase.auth.signOut()
//                startActivity(Intent(this, AuthActivity::class.java))
//                true
//            }
//            else -> super.onOptionsItemSelected(item)
//        }
//    }

//    fun onClickHome(v: View?){
//        when(v?.id){
//            R.id.btnPantallaInfo -> {
//                intent = Intent(this, infoPaciente::class.java)
//                startActivity(intent)
//            }
////            R.id.btnCitas -> {
////                Log.i("INFO", "Se ha pulsado el botón")
////
////            }
//        }
//    }
}