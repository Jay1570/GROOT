package com.example.groot

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemePreference(private val context: Context) {
    companion object {
        const val PREFS_KEY = "theme_preference"
        const val THEME_KEY = "theme"
    }

    private val sharedPreferences by lazy {
        context.applicationContext.getSharedPreferences(
            PREFS_KEY,
            Context.MODE_PRIVATE
        )
    }

    private val _themFlow by lazy { MutableStateFlow(getSavedTheme()) }
    val theme: StateFlow<Theme> by lazy { _themFlow }

    private fun getSavedTheme(): Theme {
        return Theme.fromInt(sharedPreferences.getInt(THEME_KEY, 2))
    }

    fun setTheme(theme: Theme) {
        sharedPreferences.edit().putInt(THEME_KEY, theme.toInt()).apply()
        _themFlow.value = theme
    }
}

enum class Theme {
    LIGHT,
    DARK,
    SYSTEM;

    override fun toString(): String {
        return when (this) {
            LIGHT -> "Light"
            DARK -> "Dark"
            SYSTEM -> "System"
        }
    }

    fun toInt(): Int {
        return when (this) {
            LIGHT -> 0
            DARK -> 1
            SYSTEM -> 2
        }
    }

    companion object {
        fun fromInt(value: Int): Theme {
            return when (value) {
                0 -> LIGHT
                1 -> DARK
                else -> SYSTEM
            }
        }
    }
}
