package com.example.urocareapp.modelo

data class Event(
    var name: String = "",
    var date: String = "",
    var time: String = "",
    var description: String = "",
    var id: String = "" // Campo para el ID del documento de Firestore

)
