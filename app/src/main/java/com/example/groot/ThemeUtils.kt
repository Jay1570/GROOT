package com.example.groot

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

class ThemeUtils(context: Context) {

    companion object {
        const val PREFS_NAME = "theme_prefs"
        private const val KEY_THEME = "theme"
        const val THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
        const val THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES
        const val THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveTheme(theme: Int) {
        prefs.edit().putInt(KEY_THEME, theme).apply()
    }

    fun getTheme(): Int {
        return prefs.getInt(KEY_THEME, THEME_SYSTEM)
    }
}