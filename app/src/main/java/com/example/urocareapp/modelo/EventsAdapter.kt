package com.example.urocareapp.modelo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.R

class EventsAdapter(private val events: List<Event>) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    // ViewHolder: Representa cada elemento visible en el RecyclerView
    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.eventName)
        val dateTextView: TextView = itemView.findViewById(R.id.eventDate)
        val timeTextView: TextView = itemView.findViewById(R.id.eventTime)
        val descriptionTextView: TextView = itemView.findViewById(R.id.eventDescription)
    }

    // Método que infla el layout de cada elemento de la lista
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    // Método que vincula los datos de un evento a la vista
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.nameTextView.text = event.name
        holder.dateTextView.text = event.date
        holder.timeTextView.text = event.time
        holder.descriptionTextView.text = event.description
    }

    // Retorna el número de elementos en la lista de eventos
    override fun getItemCount(): Int {
        return events.size
    }
}
