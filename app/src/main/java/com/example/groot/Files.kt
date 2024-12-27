package com.example.groot

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.StorageAdapter
import com.example.groot.model.TreeNode
import com.example.groot.viewmodel.FilesViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Files : AppCompatActivity() {

    private val viewModel: FilesViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var adapter: StorageAdapter
    private lateinit var currentRefTree: StorageReference
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var loadingOverlay: View
    private lateinit var toolbar: MaterialToolbar
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_files)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)
        path = intent.getStringExtra("path") ?: ""
        progressBar = findViewById(R.id.progressBar)
        loadingOverlay = findViewById(R.id.loadingOverlay)
        toolbar = findViewById(R.id.topAppBar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StorageAdapter(emptyList()) { item -> handleItemClick(item) }
        recyclerView.adapter = adapter

        currentRefTree = FirebaseStorage.getInstance().reference.child(path)

        viewModel.initializeRoot(path)

        viewModel.fileList.observe(this) { list ->
            adapter.update(list)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) showLoading() else hideLoading()
        }

        viewModel.error.observe(this) { error ->
            error?.let {
                hideLoading()
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.clearError()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isParentsEmpty) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                } else {
                    viewModel.navigateBack()
                    if (::searchView.isInitialized) {
                        searchView.setQuery("", false)
                    }
                }
            }
        })

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val orientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val bar = v.findViewById<Toolbar>(R.id.topAppBar)
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

    private fun handleItemClick(item: TreeNode) {
        if (item.isFolder) {
            viewModel.navigateToFolder(item)
            if (::searchView.isInitialized) {
                searchView.setQuery("", false)
            }
        } else {
            val intent = Intent(this, FileContentActivity::class.java).apply {
                putExtra("FILE_NAME", item.name)
                putExtra("PATH", item.path)
            }
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                searchView = item.actionView as SearchView
                searchView.queryHint = "Search"
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter(newText!!)
                        return true
                    }

                })
                true
            }
            else -> super.onOptionsItemSelected(item)
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
