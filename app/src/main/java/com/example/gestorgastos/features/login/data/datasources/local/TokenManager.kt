package com.example.gestorgastos.features.login.data.datasources.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "gestor_gastos_prefs",
        Context.MODE_PRIVATE
    )

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun saveUserName(name: String) {
        prefs.edit().putString("user_name", name).apply()
    }

    fun getUserName(): String? {
        return prefs.getString("user_name", null)
    }

    fun saveUserId(id: Int) {
        prefs.edit().putInt("user_id", id).apply()
    }

    fun getUserId(): Int {
        return prefs.getInt("user_id", 0)
    }

    fun saveUserProfilePic(uri: String) {
        prefs.edit().putString("user_profile_pic", uri).apply()
    }

    fun getUserProfilePic(): String? {
        return prefs.getString("user_profile_pic", null)
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
