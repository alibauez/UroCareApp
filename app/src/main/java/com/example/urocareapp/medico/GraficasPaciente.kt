package com.example.urocareapp.medico

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.example.urocareapp.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class GraficasPaciente : BaseActivityMedico() {
    private lateinit var tvPacienteNombre: TextView
    private lateinit var chartAgua: BarChart
    private lateinit var chartActividad: BarChart
    private lateinit var chartMedicacion: PieChart
    private val db = Firebase.firestore
    private lateinit var last7Days: List<String>  // Aquí defines last7Days como propiedad de la clase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graficas_paciente)

        tvPacienteNombre = findViewById(R.id.tvPacienteNombre)
        chartAgua = findViewById(R.id.chartAgua)
        chartActividad = findViewById(R.id.chartActividad)
        chartMedicacion = findViewById(R.id.chartMedicacion)

        val pacienteEmail = intent.getStringExtra("pacienteEmail")
        if (pacienteEmail.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Email de paciente no proporcionado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val btnBack: ImageButton = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
        }

        db.collection("pacientes")
            .document(pacienteEmail)
            .get()
            .addOnSuccessListener { document ->
                val nombre = document.getString("nombre") ?: "Paciente desconocido"
                val apellidos = document.getString("apellidos") ?: ""

                // Concatenar el nombre y los apellidos
                val pacienteNombreCompleto = "$nombre $apellidos".trim()

                tvPacienteNombre.text = "Paciente: $pacienteNombreCompleto"
            }
            .addOnFailureListener {
                tvPacienteNombre.text = "Paciente: Desconocido"
            }

        loadHabitData(pacienteEmail)

    }

    private fun loadHabitData(email: String) {
        val habitsCollection = db.collection("pacientes").document(email).collection("habitos")
        last7Days = getLast7Days()  // Aquí asignas last7Days

        val aguaEntries = mutableListOf<BarEntry>()
        val actividadEntries = mutableListOf<BarEntry>()
        val medicacionEntries = mutableListOf<PieEntry>()

        var yesCount = 0
        var noCount = 0

        var processedDays = 0 // Contador para verificar cuándo se han procesado todos los días

        // Carga los datos en las entradas
        for ((xAxisIndex, date) in last7Days.withIndex()) {  // Usa el índice como el valor para el eje X
            habitsCollection.document(date)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val agua = document.getDouble("agua") ?: 0.0
                        val actividad = document.getLong("actividadFisica")?.toFloat() ?: 0f

                        val medicacion = when {
                            document.getBoolean("medicacion") == true -> {
                                yesCount++
                                1
                            }
                            document.getBoolean("medicacion") == false -> {
                                noCount++
                                0
                            }
                            else -> {
                                noCount++
                                0
                            }
                        }

                        // Asignación de datos para las gráficas de barra
                        aguaEntries.add(BarEntry(xAxisIndex.toFloat(), agua.toFloat()))
                        actividadEntries.add(BarEntry(xAxisIndex.toFloat(), actividad))

                        // Asignación de datos para la gráfica de medicación (PieChart)
                        medicacionEntries.add(PieEntry(medicacion.toFloat(), if (medicacion == 1) "Sí" else "No"))
                    }

                    processedDays++

                    // Cuando todos los días hayan sido procesados, configura las gráficas
                    if (processedDays == last7Days.size) {
                        setupBarChart(chartAgua, aguaEntries, "Agua (litros)")
                        setupBarChart(chartActividad, actividadEntries, "Actividad Física (minutos)")

                        val medicacionPieEntries = listOf(
                            PieEntry(yesCount.toFloat(), "Sí"),
                            PieEntry(noCount.toFloat(), "No")
                        )
                        setupPieChart(chartMedicacion, medicacionPieEntries, "Medicación (Sí/No)")
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupBarChart(chart: BarChart, entries: List<BarEntry>, label: String) {
        val dataSet = BarDataSet(entries, label)
        dataSet.color = resources.getColor(R.color.secondary, null)
        dataSet.valueTextColor = resources.getColor(android.R.color.black, null)
        dataSet.valueTextSize = 14f

        val barData = BarData(dataSet)
        barData.barWidth = 0.3f  // Aumenta el tamaño de la barra si es necesario
        chart.data = barData
        chart.invalidate()

        // Configuración del eje X
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            labelCount = 7
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    val date = last7Days[value.toInt()]
                    val dayOfMonth = date.split("-")[2]
                    return dayOfMonth
                }
            }
            // Ajusta el rango de valores del eje X para evitar que se corten las barras
            axisMinimum = -0.5f  // Reduce el valor mínimo para dejar espacio antes de la primera barra
            axisMaximum = 6.5f  // Aumenta el valor máximo para dejar espacio después de la última barra
            textSize = 14f

            // Rotación de las etiquetas
            setLabelRotationAngle(45f)
        }

        // Ajustar margen inferior para evitar que las etiquetas se corten
        chart.setExtraBottomOffset(20f)

        // Configuración del eje Y
        chart.axisLeft.apply {
            textSize = 14f
        }

        chart.axisRight.isEnabled = false
        chart.description.text = label
        chart.legend.isEnabled = false
    }




    private fun setupPieChart(chart: PieChart, entries: List<PieEntry>, label: String) {
        val dataSet = PieDataSet(entries, label)

        // Colores para "Sí" y "No"
        val colors = listOf(
            resources.getColor(R.color.primary, null), // Color para "Sí"
            resources.getColor(R.color.red, null)    // Color para "No"
        )
        dataSet.setColors(colors)

        dataSet.valueTextColor = resources.getColor(android.R.color.black, null)
        dataSet.valueTextSize = 14f

        val pieData = PieData(dataSet)
        chart.data = pieData
        chart.invalidate()

        chart.description.text = label
        chart.legend.isEnabled = true
    }

    private fun getLast7Days(): List<String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val dates = mutableListOf<String>()

        for (i in 0 until 7) { // Genera los últimos 7 días
            dates.add(dateFormat.format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, -1)
        }
        return dates.reversed() // Reversa el orden para que el día actual sea el primero
    }
}

