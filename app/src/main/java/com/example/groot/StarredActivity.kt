package com.example.groot

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

class StarredActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_starred)

        val toolbarStar: MaterialToolbar = findViewById(R.id.topAppBar)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)

        toolbarStar.setNavigationOnClickListener {
            // Perform a custom action here, like starting a new activity
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()  // Optional: Finish the current activity if you don't want to keep it in the back stack
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}