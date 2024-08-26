package com.example.groot

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import coil.load
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FileContentActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var imageView: ImageView
    private lateinit var scrollView: ScrollView
    private lateinit var userStorageRef: StorageReference
    private var path = ""
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_file_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val toolbarRepo: MaterialToolbar = findViewById(R.id.topAppBar)
        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)


        toolbarRepo.setNavigationOnClickListener {
            finish()
        }
        textView = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)
        scrollView = findViewById(R.id.scrollableText)
        progressBar = findViewById(R.id.progressBar)
        path = intent.getStringExtra("path") ?: ""
        userStorageRef = FirebaseStorage.getInstance().reference.child(path)
        fetchAndDisplayFileContent(userStorageRef)
    }
    private fun fetchAndDisplayFileContent(fileRef: StorageReference) {
        progressBar.isVisible = true
        Log.d("FileContentActivity", "Fetching metadata for path: $path")
        val TEN_MEGABYTE: Long = 1024 * 1024 * 10

        fileRef.metadata.addOnSuccessListener { metadata ->
            val fileType = metadata.contentType ?: ""
            Log.d("FileContentActivity", "File type: $fileType")
            when {
                fileType.startsWith("image/") -> {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        imageView.isVisible = true
                        imageView.load(uri.toString())
                        progressBar.isVisible = false
                    }.addOnFailureListener { e ->
                        Log.e("FileContentActivity", "Failed to fetch image URL", e)
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                        progressBar.isVisible = false
                    }
                }
                else -> {
                    fileRef.getBytes(TEN_MEGABYTE)
                        .addOnSuccessListener { bytes ->
                            scrollView.isVisible = true
                            val fileContent = String(bytes)
                            displayFileContent(fileContent)
                            progressBar.isVisible = false
                        }
                        .addOnFailureListener { e ->
                            Log.e("FileContentActivity", "Failed to fetch file content", e)
                            Toast.makeText(this, "Failed to load file content", Toast.LENGTH_SHORT).show()
                            progressBar.isVisible = false
                        }
                }
            }
        }.addOnFailureListener { e ->
            Log.e("FileContentActivity", "Failed to fetch metadata", e)
            Toast.makeText(this, "Failed to load file content", Toast.LENGTH_SHORT).show()
            progressBar.isVisible = false
        }
    }
    private fun displayFileContent(content: String) {
        textView.text = content
    }
}