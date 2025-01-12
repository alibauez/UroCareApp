package com.example.urocareapp

import android.app.Application
import io.getstream.chat.android.client.ChatClient

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializamos el cliente de chat con la API Key
        ChatClient.Builder("9th4w3s4e33m", this).build()
    }
}
