package com.example.groot

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.RepositoryDetails
import com.example.groot.adapter.RepositoryAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RepoActivity : AppCompatActivity() {
    private val TAG = "RepositoryListActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RepositoryAdapter
    private val repositories = mutableListOf<RepositoryDetails>()
    private lateinit var userStorageRef: StorageReference
    private lateinit var username: String
    private lateinit var progressBar: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repo)

        val toolbarRepo: MaterialToolbar = findViewById(R.id.topAppBar)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)


        toolbarRepo.setNavigationOnClickListener {
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RepositoryAdapter(this, repositories) { repo ->
            openRepository(repo)
        }
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
                    repositories.add(RepositoryDetails(prefix.name, userStorageRef.name))
                }
                adapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch repositories", e)
                progressBar.visibility = View.GONE
            }
    }

    private fun openRepository(repository: RepositoryDetails) {
        /*val intent = Intent(this, MainActivity5::class.java).apply {
            putExtra("REPOSITORY_NAME", repository.name)
            putExtra("USER_NAME", username)
        }
        startActivity(intent)*/
    }
}