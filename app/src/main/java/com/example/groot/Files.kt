package com.example.groot

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.StorageAdapter
import com.example.groot.adapter.TreeViewAdapter
import com.example.groot.model.StorageItem
import com.example.groot.model.TreeNode
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Files : AppCompatActivity() {
    private val TAG = "FilesListActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StorageAdapter
    private val storageItems = mutableListOf<StorageItem>()
    private lateinit var repoRef: StorageReference
    private lateinit var currentRef: StorageReference
    private lateinit var currentRefTree: StorageReference
    private lateinit var progressBar: CircularProgressIndicator
    private lateinit var loadingOverlay: View

    private lateinit var drawerLayout: DrawerLayout
    private val rootNodeList = mutableListOf<TreeNode>()
    private lateinit var treeAdapter: TreeViewAdapter
    private lateinit var recycler_ViewDraw: RecyclerView
    private lateinit var toolbar: Toolbar

    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_files)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)
        path = intent.getStringExtra("path") ?: ""
        progressBar = findViewById(R.id.progressBar)
        loadingOverlay = findViewById(R.id.loadingOverlay)

        recycler_ViewDraw = findViewById(R.id.recycler_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.menu_alt_1_svgrepo_com)
        }

        repoRef = FirebaseStorage.getInstance().reference.child(path)
        currentRef = repoRef
        currentRefTree = FirebaseStorage.getInstance().reference.child(path)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StorageAdapter(storageItems) { item -> handleItemClick(item) }
        recyclerView.adapter = adapter

        treeAdapter = TreeViewAdapter(
            rootNodeList,
            this,
            currentRefTree
        )
        recycler_ViewDraw.layoutManager = LinearLayoutManager(this)
        recycler_ViewDraw.adapter = treeAdapter

        listFilesAndFolders(repoRef)
        fetchFirebaseData(currentRefTree, rootNodeList)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val orientation = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            val bar = v.findViewById<Toolbar>(R.id.toolbar)
            val layoutParams = bar.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.setMargins(
                layoutParams.leftMargin,
                if (orientation) layoutParams.topMargin else systemBarsInsets.top,
                systemBarsInsets.right,
                layoutParams.bottomMargin
            )
            v.findViewById<LinearLayout>(R.id.drawerMenu).updatePadding(top = systemBarsInsets.top)
            bar.layoutParams = layoutParams
            WindowInsetsCompat.CONSUMED
        }

    }

    private fun listFilesAndFolders(storageRef: StorageReference) {
        progressBar.isVisible = true
        loadingOverlay.isVisible = true
        recyclerView.isVisible = false
        storageItems.clear()
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.prefixes.forEach { prefix ->
                    if(prefix.name == ".groot" || prefix.name == ".git") {
                        return@forEach
                    }
                    storageItems.add(StorageItem(prefix.name, true))
                }
                listResult.items.forEach { item ->
                    if (item.name == "user.txt") return@forEach
                    storageItems.add(StorageItem(item.name, false))
                }
                adapter.notifyDataSetChanged()
                progressBar.isVisible = false
                loadingOverlay.isVisible = false
                recyclerView.isVisible = true
            }
            .addOnFailureListener { e ->
                progressBar.isVisible = false
                loadingOverlay.isVisible = false
                recyclerView.isVisible = true
                Log.e(TAG, "Failed to list files and folders", e)
            }
    }

    private fun handleItemClick(item: StorageItem) {
        if (item.isFolder) {
            currentRef = currentRef.child(item.name)
            listFilesAndFolders(currentRef)
        } else {
            openFile(currentRef.child(item.name), item.name)
        }
    }

    private fun openFile(fileRef: StorageReference, fileName: String) {
        progressBar.isVisible = true
        loadingOverlay.isVisible = true
        recyclerView.isVisible = false
        fileRef.metadata.addOnSuccessListener { meta ->
            if (meta.contentType!!.contains("image") || meta.name!!.endsWith(".webp")) {
                progressBar.isVisible = false
                loadingOverlay.isVisible = false
                recyclerView.isVisible = true
                Toast.makeText(this, "File Type Not Supported", Toast.LENGTH_SHORT).show()
                return@addOnSuccessListener
            }
            fileRef.getBytes(meta.sizeBytes).addOnSuccessListener { bytes ->
                val content = String(bytes)
                progressBar.isVisible = false
                loadingOverlay.isVisible = false
                recyclerView.isVisible = true
                showFileContent(fileName, content)
            }.addOnFailureListener { e ->
                progressBar.isVisible = false
                loadingOverlay.isVisible = false
                recyclerView.isVisible = true
                Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Failed to Open File", e)
            }
        }.addOnFailureListener { e ->
            progressBar.isVisible = false
            loadingOverlay.isVisible = false
            recyclerView.isVisible = true
            Log.e(TAG, "Failed to Open File", e)
            Toast.makeText(this, "Failed to open file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showFileContent(fileName: String, content: String) {

        val intent = Intent(this, FileContentActivity::class.java).apply {
            putExtra("FILE_NAME", fileName)
            putExtra("FILE_CONTENT", content)
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (currentRef != repoRef) {
            currentRef.parent?.let {
                currentRef = it
                listFilesAndFolders(currentRef)
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            R.id.search -> {
                val searchView = item.actionView as SearchView
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

    private fun fetchFirebaseData(reference: StorageReference, parentNodeList: MutableList<TreeNode>) {
        progressBar.isVisible = true
        loadingOverlay.isVisible = true
        recyclerView.isVisible = false
        recycler_ViewDraw.isVisible = false
        reference.listAll().addOnSuccessListener { listResult ->
            for (folderRef in listResult.prefixes) {
                if (folderRef.name == ".groot" || folderRef.name == ".git") {
                    continue
                }
                val folderNode = TreeNode(folderRef.name, true)
                parentNodeList.add(folderNode)
                fetchFirebaseData(folderRef, folderNode.children)
            }
            for (fileRef in listResult.items) {
                if (fileRef.name == "user.txt") continue
                val fileNode = TreeNode(fileRef.name, false)
                parentNodeList.add(fileNode)
            }
            treeAdapter.notifyDataSetChanged()
            progressBar.isVisible = false
            loadingOverlay.isVisible = false
            recyclerView.isVisible = true
            recycler_ViewDraw.isVisible = true
        }.addOnFailureListener { e ->
            progressBar.isVisible = false
            loadingOverlay.isVisible = false
            recyclerView.isVisible = true
            recycler_ViewDraw.isVisible = true
            Log.e(TAG, "Failed to fetch data from Firebase", e)
        }
    }
}