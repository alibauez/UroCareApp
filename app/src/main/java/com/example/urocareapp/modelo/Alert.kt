package com.example.a2425fibroappclase.modelo

import android.content.Context
import androidx.appcompat.app.AlertDialog

object Alert {
    fun showAlert(context: Context, text: String){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder
            .setMessage(text)
            .setTitle("Info")
            .setPositiveButton("ACEPTAR", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}