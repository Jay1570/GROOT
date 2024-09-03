package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.RepositoryListAdapter
import com.example.groot.viewmodel.StarRepoViewModel
import com.google.android.material.appbar.MaterialToolbar

class StarredActivity : AppCompatActivity() {

    private val viewModel: StarRepoViewModel by viewModels()

    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var message: TextView
    private var isEmpty = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_starred)

        val userId = intent.getStringExtra("userId") ?: ""
        if (userId.isEmpty()) viewModel.getStarRepo() else viewModel.getStarRepo(userId)

        toolbar = findViewById(R.id.topAppBar)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        message = findViewById(R.id.message)

        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = RepositoryListAdapter(repoPath = emptyList()) { onItemClick(it) }
        recyclerView.adapter = recyclerAdapter

        toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.starredRepositories.observe(this) { repo ->
            isEmpty = repo.repositories.isEmpty()
            recyclerAdapter.updatePath(repo.repositories)
            recyclerAdapter.notifyDataSetChanged()
            message.isVisible = isEmpty && !viewModel.isLoading.value!!
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
            message.isVisible = !isLoading && isEmpty
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun onItemClick(path: String) {
        val intent = Intent(this, RepoDetailsActivity::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }
}