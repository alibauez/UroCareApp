package com.example.urocareapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton

class PreparacionPostoperatorioSecundario : BaseActivity() {

    private lateinit var stepText: TextView
    private lateinit var dynamicContentLayout: LinearLayout
    private lateinit var buttonNext: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val steps = listOf(
        Triple("Control del dolor",
            "Utilizar analgésicos como paracetamol o AINEs en las primeras 48 horas. Si el dolor es intenso, considerar opioides a corto plazo y ajustar según sea necesario.",
            R.drawable.ic_analgesicos),
        Triple("Antibióticos profilácticos",
            "Administrar antibióticos según el riesgo de infección y el perfil de sensibilidad local, asegurando que el paciente complete el ciclo indicado.",
            R.drawable.ic_antibioticos),
        Triple("Monitoreo de la diuresis",
            "Vigilar la cantidad de orina y realizar análisis regulares para detectar obstrucción o infección. La hematuria es común, pero debe investigarse si persiste.",
            R.drawable.ic_orinar),
        Triple("Cuidado de las vías urinarias",
            "Si se colocó un stent o catéter, monitorizar signos de infección o obstrucción. Retirar el stent según lo indicado y tratar la disuria o cólicos con medicamentos.",
            R.drawable.ic_cateter)
    )
    private var currentStepIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preparacion_postoperatorio_secundario)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Inicializar vistas
        stepText = findViewById(R.id.stepText)
        dynamicContentLayout = findViewById(R.id.dynamicContentLayout)
        buttonNext = findViewById(R.id.buttonNext)
        progressBar = findViewById(R.id.progressBar)

        // Configurar contenido inicial
        updateStep()

        // Configurar barra de progreso
        updateProgressBar()

        // Configurar botón "Siguiente"
        buttonNext.setOnClickListener {
            currentStepIndex++
            if (currentStepIndex >= steps.size) {
                currentStepIndex = 0 // Reinicia a la primera posición si es el último paso
            }
            updateStep()
            updateProgressBar() // Actualiza la barra de progreso
        }
    }

    private fun updateStep() {
        // Actualiza el texto del subtítulo
        stepText.text = "Paso ${currentStepIndex + 1} de ${steps.size}"

        // Actualiza el contenido dinámico
        dynamicContentLayout.removeAllViews()
        val (title, description, imageRes) = steps[currentStepIndex]

        // Crear el título
        val titleTextView = TextView(this).apply {
            text = title
            textSize = 20f
            setTextColor(ContextCompat.getColor(this@PreparacionPostoperatorioSecundario, R.color.black))
            setTypeface(typeface, Typeface.BOLD)
        }

        // Crear la descripción
        val descriptionTextView = TextView(this).apply {
            text = description
            textSize = 18f
            setTextColor(Color.parseColor("#666666"))
            justificationMode = android.text.Layout.JUSTIFICATION_MODE_INTER_WORD
        }

        // Crear el ImageView
        val imageView = ImageView(this).apply {
            setImageResource(imageRes) // Asigna la imagen
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, // Ajusta el ancho al contenido
                1000 // Define una altura específica en píxeles (puedes ajustarlo según necesites)
            ).apply {
                setMargins(0, 16, 0, 16) // Márgenes alrededor de la imagen
            }
            scaleType = ImageView.ScaleType.CENTER_INSIDE // Ajusta la escala de la imagen dentro del ImageView
        }

        // Añadir el título, la descripción y la imagen al layout en el orden deseado
        dynamicContentLayout.addView(titleTextView)
        dynamicContentLayout.addView(descriptionTextView)
        dynamicContentLayout.addView(imageView)
    }

    private fun updateProgressBar() {
        // Calcula el progreso en porcentaje
        val progress = ((currentStepIndex + 1) * 100) / steps.size
        progressBar.progress = progress
    }
}
