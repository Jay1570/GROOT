package com.example.groot

import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.FileContentAdapter
import com.example.groot.viewmodel.FilesViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator

class FileContentActivity : AppCompatActivity() {

    private val viewModel: FilesViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var loadingOverlay: View

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_file_content)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        progressBar = findViewById(R.id.progressBar)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        val fileName = intent.getStringExtra("FILE_NAME") ?: "Untitled"
        val filePath = intent.getStringExtra("PATH") ?: ""

        viewModel.openFile(filePath, fileName)
        supportActionBar?.title = fileName

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.fileContent.observe(this) { content ->
            content?.let {
                val lines = content.lines().mapIndexed { index, line -> Pair(index + 1, line) }
                val adapter = FileContentAdapter(this, lines)
                recyclerView.adapter = adapter
            }
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                hideLoading()
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
                finish()
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

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
                finish()
                true
            }

            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }
    private fun showLoading() {
        progressBar.isVisible = true
        loadingOverlay.isVisible = true
        recyclerView.isVisible = false
    }

    private fun hideLoading() {
        progressBar.isVisible = false
        loadingOverlay.isVisible = false
        recyclerView.isVisible = true
    }
}