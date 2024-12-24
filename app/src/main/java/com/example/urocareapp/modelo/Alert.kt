package com.example.urocareapp.modelo

import android.content.Context
import androidx.appcompat.app.AlertDialog

object Alert {
    enum class AlertType {
        SUCCESS,
        ERROR,
        INFO
    }

    fun showAlert(context: Context, text: String, type: AlertType) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)

        // Configuración del título según el tipo de alerta
        val title = when (type) {
            AlertType.SUCCESS -> "Éxito"
            AlertType.ERROR -> "Error"
            AlertType.INFO -> "Información"
        }

        // Configuración del ícono según el tipo de alerta (opcional)
        val icon = when (type) {
            AlertType.SUCCESS -> android.R.drawable.ic_dialog_info
            AlertType.ERROR -> android.R.drawable.ic_delete
            AlertType.INFO -> android.R.drawable.ic_dialog_alert
        }

        builder
            .setMessage(text)
            .setTitle(title)
            .setIcon(icon)
            .setPositiveButton("ACEPTAR", null)

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}
