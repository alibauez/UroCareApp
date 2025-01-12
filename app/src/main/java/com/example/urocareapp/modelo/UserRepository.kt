package com.example.urocareapp.modelo

import io.getstream.chat.android.models.User


interface UserRepository {
    fun getCurrentUser(): User? // Método para obtener el usuario actual, si está guardado
    fun setCurrentUser(user: User) // Método para guardar el usuario actual
    fun clearCurrentUser() // Método para limpiar el usuario guardado
}