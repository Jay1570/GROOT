package com.example.groot

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groot.viewmodel.AuthViewModel
import com.google.android.material.appbar.MaterialToolbar

class SettingsActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    private lateinit var btnSignout: Button
    private lateinit var appBar: MaterialToolbar
    private lateinit var btnTheme: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnSignout = findViewById(R.id.btnSignOut)
        btnTheme = findViewById(R.id.btnTheme)
        appBar = findViewById(R.id.topAppBar)

        btnSignout.setOnClickListener {
            authViewModel.signOut()
            val intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        appBar.setNavigationOnClickListener {
            finish()
        }

        btnTheme.setOnClickListener {
            val themes = arrayOf("Light","Dark","System")

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Select Theme")
            builder.setItems(themes) { _, which ->
                val selectedTheme = when(which) {
                    0 -> ThemeUtils.THEME_LIGHT
                    1 -> ThemeUtils.THEME_DARK
                    2 -> ThemeUtils.THEME_SYSTEM
                    else -> ThemeUtils.THEME_SYSTEM
                }
                val themeUtils = ThemeUtils(applicationContext)
                themeUtils.saveTheme(selectedTheme)
                AppCompatDelegate.setDefaultNightMode(selectedTheme)
                Log.d("Theme", AppCompatDelegate.getDefaultNightMode().toString())
            }
            builder.show()
        }
    }
}