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

class PreparacionPreoperatorioSecundario : BaseActivity() {

    private lateinit var stepText: TextView
    private lateinit var dynamicContentLayout: LinearLayout
    private lateinit var buttonNext: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val steps = listOf(
        Triple("Evaluación de la condición médica",
            "Realizar un examen físico completo, análisis de sangre y orina, y una evaluación por imagen (como ecografía o TAC) para determinar el tamaño, ubicación y tipo de cálculo renal.",
            R.drawable.evaluacionmedica),
        Triple("Preparación para la anestesia",
            "Consultar con el anestesista para revisar historial médico, posibles alergias y la medicación actual del paciente. Asegurarse de que el paciente siga las indicaciones para el ayuno preoperatorio.",
            R.drawable.anestesia),
        Triple("Hidratación adecuada",
            "Asegurar que el paciente esté bien hidratado antes de la cirugía, a menos que se indique lo contrario. La hidratación ayuda a la dilución de los cálculos y a la reducción de complicaciones.",
            R.drawable.hidratacion),
        Triple("Antibióticos profilácticos",
            "Administrar antibióticos profilácticos antes de la cirugía para prevenir infecciones, especialmente si existe riesgo elevado debido a la presencia de infecciones urinarias previas.",
            R.drawable.ic_antibioticos)
    )
    private var currentStepIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preparacion_preoperatorio_secundario)
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
            textSize = 16f
            setTextColor(ContextCompat.getColor(this@PreparacionPreoperatorioSecundario, R.color.black))
            setTypeface(typeface, Typeface.BOLD)
        }

        // Crear la descripción
        val descriptionTextView = TextView(this).apply {
            text = description
            textSize = 14f
            setTextColor(Color.parseColor("#666666"))
        }

        // Crear el ImageView
        val imageView = ImageView(this).apply {
            setImageResource(imageRes) // Asigna la imagen
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 16) // Márgenes alrededor de la imagen
            }
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
