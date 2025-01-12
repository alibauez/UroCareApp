package com.example.urocareapp.modelo

import android.content.Context
import com.google.gson.Gson
import io.getstream.chat.android.models.User

class UserRepositoryImpl(private val context: Context) : UserRepository {

    private val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun getCurrentUser(): User? {
        val userJson = sharedPreferences.getString("current_user", null)
        return if (userJson != null) {
            gson.fromJson(userJson, User::class.java)
        } else {
            null
        }
    }

    override fun setCurrentUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString("current_user", userJson).apply()
    }

    override fun clearCurrentUser() {
        sharedPreferences.edit().remove("current_user").apply()
    }
}