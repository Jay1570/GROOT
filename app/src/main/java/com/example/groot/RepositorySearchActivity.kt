package com.example.groot

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
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
import com.example.groot.viewmodel.SearchResultsActivityViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar

class RepositorySearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var appBar: MaterialToolbar
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var message: TextView

    private val viewModel: SearchResultsActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_results)

        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)
        recyclerView = findViewById(R.id.recyclerViewSearch)
        appBar = findViewById(R.id.topAppBar)
        progressBar = findViewById(R.id.progressBar)
        message = findViewById(R.id.message)

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

        val query = intent.getStringExtra("QUERY") ?: ""
        viewModel.onRepositorySearch(query)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = RepositoryListAdapter(emptyList()) { onItemClick(it) }
        recyclerView.adapter = recyclerAdapter

        viewModel.repoList.observe(this) { repoList ->
            recyclerAdapter.updateList(repoList)
            recyclerAdapter.notifyDataSetChanged()
        }

        viewModel.error.observe(this) { error ->
            Snackbar.make(findViewById(R.id.main), error, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
            message.isVisible = !isLoading && viewModel.repoList.value?.isEmpty() ?: true
        }

        appBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun onItemClick(path: String) {
        val intent = Intent(this, RepoDetailsActivity::class.java)
        intent.putExtra("path", path)
        startActivity(intent)
    }
}