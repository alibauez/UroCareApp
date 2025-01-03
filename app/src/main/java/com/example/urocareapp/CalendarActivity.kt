package com.example.urocareapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.modelo.Event
import com.example.urocareapp.modelo.EventsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var addEventButton: Button
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var btnBack: Button
    private lateinit var textView4: TextView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // List to store events (local cache)
    private val eventsList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        calendarView = findViewById(R.id.calendarView)
        addEventButton = findViewById(R.id.addEventButton)
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)
        textView4 = findViewById(R.id.textView4)

        // Setup RecyclerView with clickListener for deletion
        eventsAdapter = EventsAdapter(eventsList) { event, position ->
            deleteEvent(event, position) // Delete event when clicked
        }
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter

        // Back button click listener
        btnBack.setOnClickListener {
            val intentBack = Intent(this, HomePaciente::class.java)
            startActivity(intentBack)
        }

        // Calendar date change listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            Toast.makeText(this, "Fecha seleccionada: $formattedDate", Toast.LENGTH_SHORT).show()
        }

        // Add event button listener
        addEventButton.setOnClickListener {
            showAddEventDialog()
        }

        // Load upcoming events
        loadUpcomingEvents()
    }

    private fun showAddEventDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.eventNameEditText)
        val dateTextView = dialogView.findViewById<TextView>(R.id.eventDateTextView)
        val timeTextView = dialogView.findViewById<TextView>(R.id.eventTimeTextView)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.eventDescriptionEditText)
        dateTextView.setTextColor(ContextCompat.getColor(this, R.color.primary)) // Cambia 'primary' al color deseado

        dateTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, R.style.CustomDatePickerTheme, { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateTextView.text = date
            }, year, month, day)

            datePickerDialog.show()
            datePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            datePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        timeTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, R.style.CustomTimePickerTheme, { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                timeTextView.text = time
            }, hour, minute, false)

            timePickerDialog.show()
            timePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.black))
            timePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Añadir Evento")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val name = nameEditText.text.toString()
                val date = dateTextView.text.toString()
                val time = timeTextView.text.toString()
                val description = descriptionEditText.text.toString()

                if (name.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && description.isNotEmpty()) {
                    val event = Event(name, date, time, description)
                    saveEventToFirebase(event)
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.secondary)) // Color del botón positivo
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.secondary)) // Color del botón negativo
    }

    private fun saveEventToFirebase(event: Event) {
        val userEmail = auth.currentUser?.email

        if (userEmail != null) {
            val eventsRef = db.collection("pacientes").document(userEmail).collection("eventos")

            eventsRef.add(event)
                .addOnSuccessListener { documentReference ->
                    event.id = documentReference.id

                    // Validar si el evento está dentro de los próximos 30 días
                    val eventDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(event.date)
                    val calendar = Calendar.getInstance()
                    val today = calendar.time
                    calendar.add(Calendar.DAY_OF_YEAR, 30)
                    val thirtyDaysLater = calendar.time

                    if (eventDate != null && eventDate in today..thirtyDaysLater) {
                        eventsList.add(event)
                        eventsAdapter.notifyItemInserted(eventsList.size - 1)
                    }

                    Toast.makeText(this, "Evento guardado correctamente", Toast.LENGTH_SHORT).show()
                    updateTextViewVisibility()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar el evento: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUpcomingEvents() {
        val userEmail = auth.currentUser?.email

        if (userEmail != null) {
            val eventsRef = db.collection("pacientes").document(userEmail).collection("eventos")

            eventsRef.get()
                .addOnSuccessListener { documents ->
                    eventsList.clear() // Limpiar la lista antes de agregar nuevos eventos

                    val calendar = Calendar.getInstance()
                    val today = calendar.time
                    calendar.add(Calendar.DAY_OF_YEAR, 30)
                    val thirtyDaysLater = calendar.time

                    for (document in documents) {
                        val event = document.toObject(Event::class.java)

                        // Asignar el ID del documento a la propiedad id del evento
                        event.id = document.id

                        val eventDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(event.date)
                        if (eventDate != null && eventDate in today..thirtyDaysLater) {
                            // Solo agregar el evento a la lista si está dentro de los próximos 30 días
                            eventsList.add(event)
                        }
                    }

                    eventsAdapter.notifyDataSetChanged()
                    updateTextViewVisibility() // Actualizar la visibilidad de TextView4 según los eventos
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al cargar eventos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el correo del usuario", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteEvent(event: Event, position: Int) {
        val userEmail = auth.currentUser?.email

        if (userEmail != null) {
            val eventRef = db.collection("pacientes").document(userEmail).collection("eventos").document(event.id)

            eventRef.delete()
                .addOnSuccessListener {
                    // Eliminar el evento de la lista local
                    eventsList.removeAt(position)
                    eventsAdapter.notifyItemRemoved(position)
                    Toast.makeText(this, "Evento eliminado correctamente", Toast.LENGTH_SHORT).show()

                    updateTextViewVisibility() // Actualizar la visibilidad de TextView4 según los eventos
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al eliminar el evento: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No se pudo obtener el correo del usuario", Toast.LENGTH_SHORT).show()
        }
    }

    // Nueva función para actualizar la visibilidad de textView4
    private fun updateTextViewVisibility() {
        if (eventsList.isNotEmpty()) {
            textView4.visibility = TextView.VISIBLE
        } else {
            textView4.visibility = TextView.GONE
        }
    }
}
