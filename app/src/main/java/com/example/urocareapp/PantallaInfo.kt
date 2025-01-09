package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge

class PantallaInfo : BaseActivity() {

    private lateinit var descriptionText: TextView
    private lateinit var indicators: List<View>
    private var currentIndex = 0
    private lateinit var textList: List<String>
    private lateinit var buttonNext: Button

    private var textRotationRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantallainfo)

        // Lista de textos que rotarán en el TextView
        textList = resources.getStringArray(R.array.text_list).toList()
        setSupportActionBar(findViewById(R.id.toolbar))

        // Vinculamos las vistas
        descriptionText = findViewById(R.id.descriptionText)
        indicators = listOf(
            findViewById(R.id.indicator1),
            findViewById(R.id.indicator2),
            findViewById(R.id.indicator3)
        )
        buttonNext = findViewById(R.id.buttonNext)

        // Llama a updateIndicators() aquí para inicializar los indicadores
        updateIndicators()

        // Iniciar la rotación de texto
        startTextRotation()

        // Configurar el evento del botón "Siguiente"
        buttonNext.setOnClickListener {
            val intent = Intent(this, PreparacionPreoperatorioSecundario::class.java)
            startActivity(intent)
        }
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
