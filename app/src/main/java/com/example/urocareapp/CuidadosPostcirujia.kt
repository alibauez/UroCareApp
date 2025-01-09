package com.example.urocareapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge

class CuidadosPostcirujia : BaseActivity() {

    private lateinit var descriptionText: TextView
    private lateinit var indicators: List<View>
    private var currentIndex = 0
    private lateinit var textList: List<String>
    private lateinit var buttonNext: Button
    private var textRotationRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cuidadospostcirujia)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Lista de textos que rotarán en el TextView
        textList = resources.getStringArray(R.array.text_list2).toList()

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
            val intent = Intent(this, PreparacionPostoperatorioSecundario::class.java)
            startActivity(intent)
        }
    }

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

    private fun updateIndicators() {
        indicators.forEachIndexed { index, view ->
            if (index == currentIndex) {
                view.setBackgroundResource(R.drawable.circle_active)
            } else {
                view.setBackgroundResource(R.drawable.circle_inactive)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        descriptionText.removeCallbacks(textRotationRunnable!!)
    }
}
