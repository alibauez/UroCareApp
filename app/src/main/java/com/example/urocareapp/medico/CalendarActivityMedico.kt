package com.example.urocareapp.medico

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.R
import com.example.urocareapp.modelo.Event
import com.example.urocareapp.modelo.EventsAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivityMedico : BaseActivityMedico() {

    private lateinit var calendarView: CalendarView
    private lateinit var addEventButton: Button
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var btnBack: Button
    private lateinit var textView4: TextView
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
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

        // Setup RecyclerView
        eventsAdapter = EventsAdapter(eventsList) { event, position ->
            deleteEvent(event, position)
        }
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter

        // Back button listener
        btnBack.setOnClickListener {
            val intentBack = Intent(this, HomeMedico::class.java)
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

        addEventButton.setOnClickListener {
            showAddEventDialog()
        }
        loadUpcomingEvents()
    }

    private fun showAddEventDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)
        val nameEditText = dialogView.findViewById<EditText>(R.id.eventNameEditText)
        val dateTextView = dialogView.findViewById<TextView>(R.id.eventDateTextView)
        val timeTextView = dialogView.findViewById<TextView>(R.id.eventTimeTextView)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.eventDescriptionEditText)

        dateTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                dateTextView.text = date
            }, year, month, day)
            datePickerDialog.show()
        }

        timeTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                timeTextView.text = time
            }, hour, minute, false)
            timePickerDialog.show()
        }

        AlertDialog.Builder(this)
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
            .show()
    }

    private fun saveEventToFirebase(event: Event) {
        val userEmail = auth.currentUser?.email ?: return
        db.collection("medicos").document(userEmail).get()
            .addOnSuccessListener { document ->
                val collection = if (document.exists()) "medicos" else "pacientes"
                val eventsRef = db.collection(collection).document(userEmail).collection("eventos")
                saveEventInCollection(eventsRef, event)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al determinar colección", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveEventInCollection(eventsRef: CollectionReference, event: Event) {
        eventsRef.add(event)
            .addOnSuccessListener { documentReference ->
                event.id = documentReference.id
                if (isWithinNext30Days(event.date)) { // Filtrar por los próximos 30 días
                    eventsList.add(event)
                    eventsAdapter.notifyItemInserted(eventsList.size - 1)
                    updateTextViewVisibility()
                }
                Toast.makeText(this, "Evento guardado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar el evento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun isWithinNext30Days(dateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val eventDate = dateFormat.parse(dateString)
            val currentDate = Calendar.getInstance().time
            val futureDate = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 30)
            }.time
            eventDate != null && eventDate in currentDate..futureDate
        } catch (e: Exception) {
            false
        }
    }


    private fun loadUpcomingEvents() {
        val userEmail = auth.currentUser?.email ?: return
        db.collection("medicos").document(userEmail).get()
            .addOnSuccessListener { document ->
                val collection = if (document.exists()) "medicos" else "pacientes"
                val eventsRef = db.collection(collection).document(userEmail).collection("eventos")
                loadEventsFromCollection(eventsRef)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al determinar colección", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadEventsFromCollection(eventsRef: CollectionReference) {
        eventsRef.get()
            .addOnSuccessListener { documents ->
                eventsList.clear()
                val currentDate = Calendar.getInstance().time
                val futureDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 30)
                }.time
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                for (document in documents) {
                    val event = document.toObject(Event::class.java)
                    event.id = document.id
                    try {
                        // Parse the event date and compare
                        val eventDate = dateFormat.parse(event.date)
                        if (eventDate != null && eventDate in currentDate..futureDate) {
                            eventsList.add(event)
                        }
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error al procesar la fecha del evento", Toast.LENGTH_SHORT).show()
                    }
                }
                eventsAdapter.notifyDataSetChanged()
                updateTextViewVisibility()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al cargar eventos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun deleteEvent(event: Event, position: Int) {
        val userEmail = auth.currentUser?.email ?: return
        db.collection("medicos").document(userEmail).get()
            .addOnSuccessListener { document ->
                val collection = if (document.exists()) "medicos" else "pacientes"
                val eventsRef = db.collection(collection).document(userEmail).collection("eventos")
                deleteEventFromCollection(eventsRef, event, position)
            }
    }

    private fun deleteEventFromCollection(eventsRef: CollectionReference, event: Event, position: Int) {
        eventsRef.document(event.id).delete()
            .addOnSuccessListener {
                eventsList.removeAt(position)
                eventsAdapter.notifyItemRemoved(position)
                updateTextViewVisibility()
                Toast.makeText(this, "Evento eliminado correctamente", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al eliminar el evento: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateTextViewVisibility() {
        if (eventsList.isEmpty()) {
            textView4.visibility = TextView.GONE
        } else {
            textView4.visibility = TextView.VISIBLE
        }
    }
}
