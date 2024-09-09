package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.StorageAdapter
import com.example.groot.fragments.TreeViewAdapter
import com.example.groot.model.StorageItem
import com.example.groot.model.TreeNode
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Files : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StorageAdapter
    private val storageItems = mutableListOf<StorageItem>()
    private lateinit var repoRef: StorageReference
    private lateinit var currentRef: StorageReference
    private lateinit var currentRefTree: StorageReference

    private lateinit var drawerLayout: DrawerLayout
    private val rootNodeList = mutableListOf<TreeNode>()
    private lateinit var treeAdapter: TreeViewAdapter
    private lateinit var recycler_ViewDraw: RecyclerView

    private lateinit var username: String
    private lateinit var repositoryName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        setContentView(R.layout.activity_files)
        username = intent.getStringExtra("USERNAME") ?: "defaultUsername"
        repositoryName = intent.getStringExtra("REPOSITORY_NAME") ?: "defaultRepository"

        recycler_ViewDraw = findViewById(R.id.recycler_view)
        drawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.menu_alt_1_svgrepo_com) // Set custom icon
        }

        // Initialize StorageReference to point to repositoryName directory
        repoRef = FirebaseStorage.getInstance().reference.child(username).child(repositoryName)
        currentRef = repoRef
        currentRefTree = FirebaseStorage.getInstance().reference.child(username)

        // Set up main RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = StorageAdapter(storageItems) { item -> handleItemClick(item) }
        recyclerView.adapter = adapter

        treeAdapter = TreeViewAdapter(rootNodeList, this, currentRefTree)
        recycler_ViewDraw.layoutManager = LinearLayoutManager(this)
        recycler_ViewDraw.adapter = treeAdapter

        // Fetch data for main RecyclerView
        listFilesAndFolders(repoRef)
        fetchFirebaseData(currentRefTree, rootNodeList)
    }

    private fun listFilesAndFolders(storageRef: StorageReference) {
        storageItems.clear()
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                listResult.prefixes.forEach { prefix ->
                    storageItems.add(StorageItem(prefix.name, true))
                }
                listResult.items.forEach { item ->
                    storageItems.add(StorageItem(item.name, false))
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
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
        fileRef.getBytes(Long.MAX_VALUE)
            .addOnSuccessListener { bytes ->
                val content = String(bytes)
                showFileContent(fileName, content)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to open file", e)
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
        menuInflater.inflate(R.menu.menu_main, menu)
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
            R.id.action_search -> {
                // Handle search action
                true
            }
            R.id.action_share -> {
                // Handle share action
                true
            }
            R.id.action_more -> {
                // Handle more action
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun fetchFirebaseData(reference: StorageReference, parentNodeList: MutableList<TreeNode>) {
        reference.listAll().addOnSuccessListener { listResult ->
            for (folderRef in listResult.prefixes) {
                val folderNode = TreeNode(folderRef.name, true)
                parentNodeList.add(folderNode)
                fetchFirebaseData(folderRef, folderNode.children)
            }

            for (fileRef in listResult.items) {
                val fileNode = TreeNode(fileRef.name, false)
                parentNodeList.add(fileNode)
            }

            treeAdapter.notifyDataSetChanged()
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to fetch data from Firebase", e)
        }
    }
}