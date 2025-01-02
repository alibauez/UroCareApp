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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.modelo.Event
import com.example.urocareapp.modelo.EventsAdapter
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var addEventButton: Button
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private lateinit var btnBack: Button

    // List to store events
    private val eventsList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        calendarView = findViewById(R.id.calendarView)
        addEventButton = findViewById(R.id.addEventButton)
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)

        val intentBack = Intent(this, HomePaciente::class.java)

        // Set up RecyclerView
        eventsAdapter = EventsAdapter(eventsList)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter

        // Back button click listener
        btnBack.setOnClickListener {
            startActivity(intentBack)
        }

        // Set calendar date change listener
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(year, month, dayOfMonth)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = dateFormat.format(selectedDate.time)
            Toast.makeText(this, "Fecha seleccionada: $formattedDate", Toast.LENGTH_SHORT).show()
        }

        // Set event button click listener
        addEventButton.setOnClickListener {
            showAddEventDialog()
        }

        // Load events for the next 30 days
        loadUpcomingEvents()
    }

    private fun showAddEventDialog() {
        // Inflate the layout for the dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_event, null)

        // Find the TextView elements in the dialog layout
        val nameEditText = dialogView.findViewById<EditText>(R.id.eventNameEditText)
        val dateTextView = dialogView.findViewById<TextView>(R.id.eventDateTextView)
        val timeTextView = dialogView.findViewById<TextView>(R.id.eventTimeTextView)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.eventDescriptionEditText)

        // Set listeners for date and time selection
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

        // Create the dialog
        val dialog = AlertDialog.Builder(this)
            .setTitle("Añadir Evento")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                // Get the input values
                val name = nameEditText.text.toString()
                val date = dateTextView.text.toString()
                val time = timeTextView.text.toString()
                val description = descriptionEditText.text.toString()

                // Validate inputs
                if (name.isNotEmpty() && date.isNotEmpty() && time.isNotEmpty() && description.isNotEmpty()) {
                    val newEvent = Event(name, date, time, description)
                    eventsList.add(newEvent)
                    eventsAdapter.notifyDataSetChanged()
                    Toast.makeText(this, "Evento añadido", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        // Show the dialog
        dialog.show()
    }

    private fun loadUpcomingEvents() {
        // Here you can filter events that occur within the next 30 days
        val calendar = Calendar.getInstance()
        val today = calendar.time
        calendar.add(Calendar.DAY_OF_YEAR, 30)
        val thirtyDaysLater = calendar.time

        // You can filter events based on the dates (using SimpleDateFormat and comparing dates)
        // Add logic here to filter events in `eventsList` that are within the 30-day range

        // Refresh the RecyclerView
        eventsAdapter.notifyDataSetChanged()
    }
}