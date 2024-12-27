package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

//        setSupportActionBar(findViewById(R.id.toolbar))
class HomePaciente : BaseActivity() {
    override val currentScreen: Int = R.id.nav_home
    private lateinit var btnPantallaInfo: Button
//    private lateinit var btnCitas: Button
//    private lateinit var btnPruebas: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_paciente)


        btnPantallaInfo = findViewById(R.id.btnPantallaInfo)
        btnPantallaInfo.setOnClickListener {
            val intent = Intent(this, PantallaInfo::class.java)
            startActivity(intent)
        }
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Lógica para el ítem "Home"
                    true
                }

                R.id.nav_profile -> {
                    // Lógica para el ítem "Profile"
                    true
                }

                R.id.nav_calendar -> {
                    // Lógica para el ítem "Settings"
                    true
                }

                R.id.nav_info -> {
                    val intent = Intent(this, PantallaInfo::class.java)
                    startActivity(intent)
                    true
                }

                else -> false
            }
        }

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