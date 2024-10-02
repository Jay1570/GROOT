package com.example.groot

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.FileContentAdapter
import com.google.android.material.appbar.MaterialToolbar

class FileContentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var recyclerView: RecyclerView
        lateinit var adapter: FileContentAdapter
        enableEdgeToEdge()
        setContentView(R.layout.activity_file_content)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val fileName = intent.getStringExtra("FILE_NAME") ?: "Untitled"
        supportActionBar?.title = fileName

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val content = intent.getStringExtra("FILE_CONTENT") ?: ""
        val lines = content.lines().mapIndexed { index, line -> Pair(index + 1, line) }

        // Pass the context to the adapter
        adapter = FileContentAdapter(this, lines)
        recyclerView.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val orientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val bar = v.findViewById<MaterialToolbar>(R.id.toolbar)
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

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
}