package com.example.urocareapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner

class RegistroPaciente : BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)
        setSupportActionBar(findViewById(R.id.toolbar))

        // Llenar Spinner de Género
        val genderSpinner: Spinner = findViewById(R.id.spinnerGender)
        val genderOptions = listOf("Masculino", "Femenino", "Otro")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        genderSpinner.adapter = genderAdapter

        // Llenar Spinner de Grupo Sanguíneo
        val bloodGroupSpinner: Spinner = findViewById(R.id.spinnerBloodGroup)
        val bloodGroupOptions = listOf("0-", "0+", "A-", "A+", "B-", "B+", "AB-", "AB+")
        val bloodGroupAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bloodGroupOptions)
        bloodGroupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bloodGroupSpinner.adapter = bloodGroupAdapter

    }
}