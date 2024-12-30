package com.example.urocareapp

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.R
import com.example.urocareapp.modelo.Event
import com.example.urocareapp.modelo.EventsAdapter
import java.text.SimpleDateFormat
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var addEventButton: Button
    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter

    // List to store events
    private val eventsList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        // Initialize views
        calendarView = findViewById(R.id.calendarView)
        addEventButton = findViewById(R.id.addEventButton)
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView)

        // Set up RecyclerView
        eventsAdapter = EventsAdapter(eventsList)
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsRecyclerView.adapter = eventsAdapter

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
        // Here you can create a dialog to input event details like name, date, time, and description
        // For simplicity, you can add a dummy event
        val event = Event("Evento de prueba", "01/01/2024", "10:00 AM", "Descripción del evento")
        eventsList.add(event)
        eventsAdapter.notifyDataSetChanged()
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
