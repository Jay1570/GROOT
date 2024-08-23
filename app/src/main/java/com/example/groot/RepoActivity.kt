package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.Repository
import com.example.groot.adapter.RepositoryAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class RepoActivity : AppCompatActivity() {

    private val TAG = "RepositoryListActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RepositoryAdapter
    private val repositories = mutableListOf<Repository>()
    private lateinit var userStorageRef: StorageReference
    private lateinit var path: String
    private lateinit var progressBar: View
    private lateinit var message: TextView

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
        message = findViewById(R.id.message)

        adapter = RepositoryAdapter(this, repositories, onRepoClick = { openRepository(it) }, onFileClick =  {openFile(it)})
        recyclerView.adapter = adapter

        progressBar = findViewById(R.id.progressBar)

        path = intent.getStringExtra("path") ?: ""
        Log.i(TAG,path)
        userStorageRef = FirebaseStorage.getInstance().reference.child(path)
        fetchRepositories()
    }

    private fun fetchRepositories() {
        progressBar.isVisible = true
        userStorageRef.listAll()
            .addOnSuccessListener { listResult ->
                repositories.clear()
                if (listResult.prefixes.isEmpty() && listResult.items.isEmpty()) {
                    progressBar.isVisible = false
                    message.isVisible = true
                    return@addOnSuccessListener
                }
                listResult.prefixes.forEach { prefix ->
                    repositories.add(Repository(prefix.name, userStorageRef.name))
                }
                listResult.items.forEach { item ->
                    repositories.add(Repository(item.name, userStorageRef.name, true))
                }
                adapter.notifyDataSetChanged()
                progressBar.isVisible = false
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to fetch repositories", e)
                progressBar.isVisible = false
            }
    }

    private fun openRepository(repository: Repository) {
        path = path + "/" + repository.name
        val intent = Intent(this, RepoActivity::class.java).apply {
            putExtra("path", path)
        }
        startActivity(intent)
    }

    private fun openFile(repository: Repository) {
        path = path + "/" + repository.name
        val intent = Intent(this, FileContentActivity::class.java).apply {
            putExtra("path", path)
        }
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        path = path.substringBeforeLast("/")
        Log.i(TAG, path)
    }
}