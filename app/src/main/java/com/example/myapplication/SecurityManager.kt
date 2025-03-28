package com.example.myapplication

import android.content.Context
import android.content.SharedPreferences

class SecurityManager(private val context: Context) {
    private val sharedPref: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    // Parolni saqlash funksiyasi
    fun savePassword(password: String) {
        sharedPref.edit().putString("AppPassword", password).apply()
    }

    // Parol to‘g‘ri yoki noto‘g‘ri ekanligini tekshirish
    fun checkPassword(password: String): Boolean {
        val savedPassword = sharedPref.getString("AppPassword", null)
        return savedPassword == password
    }

    // Parol o‘rnatilganmi yoki yo‘qmi, shuni tekshirish
    fun isPasswordSet(): Boolean {
        return sharedPref.contains("AppPassword")
    }

    // Noto‘g‘ri urinishlar sonini saqlash
    fun incrementFailedAttempts() {
        val attempts = sharedPref.getInt("FailedAttempts", 0) + 1
        sharedPref.edit().putInt("FailedAttempts", attempts).apply()
        if (attempts >= 3) {
            blockApp()
        }
    }

    // Ilovani bloklash (1 soatga)
    private fun blockApp() {
        val blockTime = System.currentTimeMillis() + 60 * 60 * 1000 // 1 soat
        sharedPref.edit()
            .putBoolean("IsBlocked", true)
            .putLong("BlockTime", blockTime)
            .putInt("FailedAttempts", 0) // urinishlarni tozalash
            .apply()
    }

    // Blok holatini tekshirish
    fun isBlocked(): Boolean {
        val isBlocked = sharedPref.getBoolean("IsBlocked", false)
        if (isBlocked) {
            val blockTime = sharedPref.getLong("BlockTime", 0)
            if (System.currentTimeMillis() > blockTime) {
                sharedPref.edit()
                    .putBoolean("IsBlocked", false)
                    .remove("BlockTime")
                    .apply()
                return false
            }
            return true
        }
        return false
    }

    // Blok holatini matn sifatida olish
    fun getLockStatus(): String {
        return if (isBlocked()) "Bloklangan" else "Yo‘q"
    }
}
