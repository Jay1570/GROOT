package com.example.groot

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groot.utility.ThemeUtils
import com.example.groot.viewmodel.AuthViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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

        btnSignout = findViewById(R.id.btnSignOut)
        btnTheme = findViewById(R.id.btnTheme)
        appBar = findViewById(R.id.topAppBar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets.getInsets(WindowInsetsCompat.Type.ime())
            val orientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val bar = v.findViewById<MaterialToolbar>(R.id.topAppBar)
            val layoutParams = bar.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin,
                if (orientation) layoutParams.topMargin else systemBarsInsets.top,
                systemBarsInsets.right,
                layoutParams.bottomMargin
            )
            bar.layoutParams = layoutParams
            WindowInsetsCompat.CONSUMED
        }

        val themeUtils = ThemeUtils(applicationContext)

        btnSignout.setOnClickListener {
            authViewModel.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        appBar.setNavigationOnClickListener {
            finish()
        }

        btnTheme.setOnClickListener {
            val themes = arrayOf("Light","Dark","System")
            var selectedTheme = themeUtils.getTheme()
            val selected = when(selectedTheme) {
                ThemeUtils.THEME_LIGHT -> 0
                ThemeUtils.THEME_DARK -> 1
                else -> 2
            }
            val builder = MaterialAlertDialogBuilder(this)
            builder
                .setTitle("Select Theme")
                .setSingleChoiceItems(themes, selected) {_, which ->
                    selectedTheme = when(which) {
                        0 -> ThemeUtils.THEME_LIGHT
                        1 -> ThemeUtils.THEME_DARK
                        else -> ThemeUtils.THEME_SYSTEM
                    }
                }
                .setPositiveButton(R.string.done) { _, _ ->
                    themeUtils.saveTheme(selectedTheme)
                    AppCompatDelegate.setDefaultNightMode(selectedTheme)
                }
                .setNegativeButton(R.string.cancel) { dialog, _ ->
                    dialog.cancel()
                }
                .setCancelable(false)
                .show()
        }
    }
}