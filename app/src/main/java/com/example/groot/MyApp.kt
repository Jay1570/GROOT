package com.example.groot

import android.app.Application

class MyApp : Application() {

    lateinit var themePreference: ThemePreference

    override fun onCreate() {
        super.onCreate()
        themePreference = ThemePreference(this)
    }
}