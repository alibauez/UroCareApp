package com.example.urocareapp.medico

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.urocareapp.R

class PacientesAdapter(
    private val pacientes: List<Paciente>,
    private val onItemClick: (Paciente) -> Unit
) : RecyclerView.Adapter<PacientesAdapter.PacienteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PacienteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_paciente, parent, false)
        return PacienteViewHolder(view)
    }

    override fun onBindViewHolder(holder: PacienteViewHolder, position: Int) {
        val paciente = pacientes[position]
        holder.bind(paciente, onItemClick)
    }

    override fun getItemCount() = pacientes.size

    class PacienteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tvPacienteNombre)

        fun bind(paciente: Paciente, onItemClick: (Paciente) -> Unit) {
            tvNombre.text = paciente.nombreCompleto
            itemView.setOnClickListener { onItemClick(paciente) }
        }
    }
}