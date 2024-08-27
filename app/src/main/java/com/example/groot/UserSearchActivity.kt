package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
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

class UserSearchActivity : AppCompatActivity() {

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
        window.navigationBarColor = getColor(R.color.md_theme_surfaceContainer)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerViewSearch)
        appBar = findViewById(R.id.topAppBar)
        progressBar = findViewById(R.id.progressBar)
        message = findViewById(R.id.message)

        val query = intent.getStringExtra("QUERY") ?: ""
        viewModel.onUserSearch(query)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = UserListRecyclerViewAdapter(emptyList()) { onItemClick(it) }
        recyclerView.adapter = recyclerAdapter

        viewModel.userList.observe(this) { users ->
            recyclerAdapter.updateUsers(users)
            recyclerAdapter.notifyDataSetChanged()
        }

        viewModel.error.observe(this) { error ->
            Snackbar.make(findViewById(R.id.main), error, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
            message.isVisible = !isLoading && viewModel.userList.value?.isEmpty() ?: true
        }

        appBar.setNavigationOnClickListener {
            onNavigateUp()
        }
    }

    private fun onItemClick(userId: String) {
        val intent = Intent(this, UserActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}