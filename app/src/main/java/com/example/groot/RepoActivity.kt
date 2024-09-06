package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
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
    private val repositories = mutableListOf<Repository>()
    private lateinit var adapter: RepositoryListAdapter
    private lateinit var userStorageRef: StorageReference
    private lateinit var username: String
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repo)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomPadding = if (!insets.isVisible(WindowInsetsCompat.Type.ime())) systemBarsInsets.bottom else 0
            v.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                bottomPadding
            )
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(bottom = imeInsets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }

        val toolbarRepo: MaterialToolbar = findViewById(R.id.topAppBar)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)


        toolbarRepo.setNavigationOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
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