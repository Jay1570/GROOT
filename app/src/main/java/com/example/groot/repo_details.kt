package com.example.groot


import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.card.MaterialCardView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin
import java.nio.charset.StandardCharsets

class repo_details : AppCompatActivity() {

    private val TAG = "repo_details"
    private lateinit var readmeTextView: TextView
    private lateinit var markwon: Markwon
    private lateinit var progressBar: ProgressBar
    private lateinit var btnCode: MaterialCardView
    private lateinit var path: String
    private lateinit var maincontent: ScrollView
    private lateinit var languageBarChartView: LanguageBarChartView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_details)

        btnCode = findViewById(R.id.btn_code)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        maincontent = findViewById(R.id.maincontent)
        languageBarChartView = findViewById(R.id.languageBarChartView)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""

        intent?.let {
            path = it.getStringExtra("path") ?: ""
        }

        val username = path.substringBefore("/").trim()
        val repoName = path.substringAfter("/").trim()

        findViewById<TextView>(R.id.txt_username).text = username
        findViewById<TextView>(R.id.txt_repo_name).text = repoName

        readmeTextView = findViewById(R.id.readme_text)
        markwon = Markwon.builder(this)
            .usePlugin(ImagesPlugin.create())
            .build()

        progressBar = findViewById(R.id.progressBar)

        /*btnCode.setOnClickListener {
            val intent = Intent(this@MainActivity5, MainActivity::class.java).apply {
                putExtra("USERNAME", username)
                putExtra("REPOSITORY_NAME", repoName)
            }
            startActivity(intent)
        }*/
        progressBar.visibility = View.VISIBLE
        maincontent.visibility = View.GONE

        searchReadmeFile(path)
        fetchFilesAndCalculateContributions(path)
    }

    private fun searchReadmeFile(path: String) {
        val storageReference = FirebaseStorage.getInstance().reference.child(path)

        searchReadmeInDirectory(storageReference)
    }

    private fun searchReadmeInDirectory(directory: StorageReference) {
        directory.listAll().addOnSuccessListener { listResult ->
            listResult.items.forEach {
                if(it.name == "README.md") {
                    fetchReadmeFile(it)
                    return@addOnSuccessListener
                }
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error listing items in directory", exception)
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Error listing items in directory", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchReadmeFile(readmeRef: StorageReference) {
        val ONE_MEGABYTE: Long = 1024 * 1024
        Log.d(TAG, "Fetching README.md from: ${readmeRef.path}")

        readmeRef.getBytes(ONE_MEGABYTE).addOnSuccessListener { bytes ->
            val readmeContent = String(bytes, StandardCharsets.UTF_8)
            Log.d(TAG, "README.md content: $readmeContent")
            markwon.setMarkdown(readmeTextView, readmeContent)
            progressBar.visibility = View.GONE
            maincontent.visibility = View.VISIBLE
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error fetching README.md file", exception)
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Error fetching README.md file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFilesAndCalculateContributions(path:String) {
        val storageReference = FirebaseStorage.getInstance().reference.child(path)

        Log.d(TAG, "Fetching files from: ${storageReference.path}")

        fetchFilesInDirectory(storageReference) { files ->
            if (files.isNotEmpty()) {
                val contributions = calculateLanguageContributions(files)
                displayContributions(contributions)
            } else {
                Log.w(TAG, "No files found in directory")
                Toast.makeText(this, "No files found in the directory.", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
                maincontent.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchFilesInDirectory(directory: StorageReference, callback: (List<StorageReference>) -> Unit) {
        Log.d(TAG, "Listing items in directory: ${directory.path}")

        directory.listAll().addOnSuccessListener { listResult ->
            val allFiles = ArrayList(listResult.items)
            val prefixes = listResult.prefixes

            Log.d(TAG, "Found ${prefixes.size} folders and ${allFiles.size} files")

            if (prefixes.isEmpty()) {
                callback(allFiles)
            } else {
                var pendingPrefixes = prefixes.size
                prefixes.forEach { folderRef ->
                    fetchFilesInDirectory(folderRef) { files ->
                        allFiles.addAll(files)
                        if (--pendingPrefixes == 0) {
                            callback(allFiles)
                        }
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error listing items in directory", exception)
            // Hide progress bar and show an error message if needed
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Error listing items in directory", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFileLanguage(fileName: String): String {
        return fileName.substringAfter(".")
    }

    private fun calculateLanguageContributions(files: List<StorageReference>): Map<String, Float> {
        val languageCount = mutableMapOf<String, Int>()
        files.forEach { fileRef ->
            val language = getFileLanguage(fileRef.name)
            languageCount[language] = (languageCount[language] ?: 0) + 1
        }

        val totalFiles = files.size
        return languageCount.mapValues { (it.value * 100f) / totalFiles }
    }

    private fun displayContributions(contributions: Map<String, Float>) {
        contributions.forEach { (language, percentage) ->
            Log.d(TAG, "Language: $language, Contribution: $percentage%")
        }

        if (::languageBarChartView.isInitialized) {
            Log.d(TAG, "Setting data to LanguageBarChartView: $contributions")
            languageBarChartView.setLanguagesData(contributions)
        } else {
            Log.e(TAG, "LanguageBarChartView is not initialized")
        }

        progressBar.visibility = View.GONE
        maincontent.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}
