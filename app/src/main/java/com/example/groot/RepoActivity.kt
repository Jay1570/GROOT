package com.example.groot

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.RepositoryListAdapter
import com.example.groot.model.Repository
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RepoActivity : AppCompatActivity() {

    private val TAG = "RepositoryListActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: MaterialToolbar
    private val repositories = mutableListOf<Repository>()
    private lateinit var adapter: RepositoryListAdapter
    private lateinit var userStorageRef: StorageReference
    private lateinit var username: String
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repo)

        toolbar = findViewById(R.id.topAppBar)
        recyclerView = findViewById(R.id.recyclerView)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        toolbar.setNavigationOnClickListener {
            finish()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RepositoryListAdapter(repo = repositories) { openRepository(it) }
        recyclerView.adapter = adapter

        progressBar = findViewById(R.id.progressBar)

        username = intent.getStringExtra("username") ?: ""
        userStorageRef = FirebaseStorage.getInstance().reference.child(username)
        fetchRepositories()
    }

    private fun fetchRepositories() {
        progressBar.visibility = View.VISIBLE
        userStorageRef.listAll()
            .addOnSuccessListener { listResult ->
                repositories.clear()
                listResult.prefixes.forEach { prefix ->
                    repositories.add(Repository(name = prefix.name.trim(), owner = username.trim()))
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch repositories", e)
                progressBar.visibility = View.GONE
            }
    }

    private fun openRepository(path: String) {
        val intent = Intent(this, RepoDetailsActivity::class.java).apply {
            putExtra("path", path)
        }
        startActivity(intent)
    }
}