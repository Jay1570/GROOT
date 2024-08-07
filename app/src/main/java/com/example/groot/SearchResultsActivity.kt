package com.example.groot

import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.UserListRecyclerViewAdapter
import com.example.groot.viewmodel.SearchResultsActivityViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar

class SearchResultsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var appBar: MaterialToolbar
    private lateinit var progressBar: CircularProgressIndicator

    private val viewModel: SearchResultsActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_results)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewSearch)
        appBar = findViewById(R.id.topAppBar)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = UserListRecyclerViewAdapter(emptyList())
        recyclerView.adapter = recyclerAdapter

        viewModel.userList.observe(this) { users ->
            recyclerAdapter.updateUsers(users)
            recyclerAdapter.notifyDataSetChanged()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
        }

        appBar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY) ?: ""
            viewModel.onSearch(query)
            Snackbar.make(findViewById(R.id.main), "Search Query :- $query", Snackbar.LENGTH_SHORT).show()
        }
    }
}