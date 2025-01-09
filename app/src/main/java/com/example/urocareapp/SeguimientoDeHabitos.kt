package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.urocareapp.medico.ListaPacientes

class SeguimientoDeHabitos : BaseActivity() {

    private lateinit var descriptionText: TextView
    private lateinit var indicators: List<View>
    private var currentIndex = 0
    private lateinit var textList: List<String>
    private var textRotationRunnable: Runnable? = null
    private lateinit var buttonNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seguimiento_de_habitos)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Lista de textos que rotarán en el TextView
        textList = resources.getStringArray(R.array.text_list4).toList()
        buttonNext = findViewById(R.id.buttonNext)
        // Vinculamos las vistas
        descriptionText = findViewById(R.id.descriptionText)
        indicators = listOf(
            findViewById(R.id.indicator1),
            findViewById(R.id.indicator2),
            findViewById(R.id.indicator3)
        )

        buttonNext.setOnClickListener {
            val intent = Intent(this, HabitosPaciente::class.java)
            startActivity(intent)
        }

        // Inicializamos los indicadores
        updateIndicators()

        // Iniciar la rotación de texto
        startTextRotation()
    }

    // Método para iniciar el cambio de texto cada 3 segundos
    private fun startTextRotation() {
        textRotationRunnable = object : Runnable {
            override fun run() {
                descriptionText.text = textList[currentIndex]
                updateIndicators()
                currentIndex = (currentIndex + 1) % textList.size
                descriptionText.postDelayed(this, 3000)
            }
        }
        descriptionText.post(textRotationRunnable!!)
    }

    // Método para actualizar los indicadores
    private fun updateIndicators() {
        indicators.forEachIndexed { index, view ->
            if (index == currentIndex) {
                view.setBackgroundResource(R.drawable.circle_active)
            } else {
                view.setBackgroundResource(R.drawable.circle_inactive)
            }
        }
    }

    // Evitar fugas de memoria al eliminar el runnable cuando la actividad se destruya
    override fun onDestroy() {
        super.onDestroy()
        descriptionText.removeCallbacks(textRotationRunnable!!)
    }
}
