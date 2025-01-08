package com.example.urocareapp.modelo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.R

class ConsejosAdapter(private val consejosList: MutableList<Consejo>) :
    RecyclerView.Adapter<ConsejosAdapter.ConsejoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsejoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_consejo, parent, false)
        return ConsejoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConsejoViewHolder, position: Int) {
        val consejo = consejosList[position]
        holder.tituloTextView.text = consejo.titulo
        holder.descripcionTextView.text = consejo.descripcion
    }

    override fun getItemCount() = consejosList.size

    // Método para actualizar la lista de consejos
    fun actualizarConsejos(nuevosConsejos: List<Consejo>) {
        consejosList.clear()
        consejosList.addAll(nuevosConsejos)
        notifyDataSetChanged()
    }

    class ConsejoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tituloTextView: TextView = itemView.findViewById(R.id.tituloTextView)
        val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
    }
}
