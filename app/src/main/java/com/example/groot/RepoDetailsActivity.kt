package com.example.groot

import android.os.Bundle
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import com.example.groot.viewmodel.RepoDetailsViewModel
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin

class RepoDetailsActivity : AppCompatActivity() {

    private val viewModel: RepoDetailsViewModel by viewModels()

    private lateinit var txtUsername: TextView
    private lateinit var txtReponame: TextView
    private lateinit var readmeTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnCode: MaterialCardView
    private lateinit var toolbar: Toolbar
    private lateinit var maincontent: ScrollView
    private lateinit var languageBarChartView: LanguageBarChartView

    private lateinit var markwon: Markwon
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_details)

        txtUsername = findViewById(R.id.txt_username)
        txtReponame = findViewById(R.id.txt_repo_name)
        readmeTextView = findViewById(R.id.readme_text)
        progressBar = findViewById(R.id.progressBar)
        btnCode = findViewById(R.id.btn_code)
        toolbar = findViewById(R.id.toolbar)
        maincontent = findViewById(R.id.maincontent)
        languageBarChartView = findViewById(R.id.languageBarChartView)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        path = intent.getStringExtra("path") ?: ""

        viewModel.fetchReadmeAndContributions(path)

        val username = path.substringBefore("/").trim()
        val repoName = path.substringAfter("/").trim()

        txtUsername.text = username
        txtReponame.text = repoName

        markwon = Markwon.builder(this).usePlugin(ImagesPlugin.create()).build()

        viewModel.languageContributions.observe(this) { contributions ->
            languageBarChartView.setLanguagesData(contributions)
        }

        viewModel.error.observe(this) { error ->
            Snackbar.make(findViewById(R.id.main), error ?: "Unknown Error", Snackbar.LENGTH_SHORT).show()
        }

        viewModel.readmeContent.observe(this) { content ->
            markwon.setMarkdown(readmeTextView, content)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
            maincontent.isVisible = !isLoading
        }

        /*btnCode.setOnClickListener {
            val intent = Intent(this@MainActivity5, MainActivity::class.java).apply {
                putExtra("path", path)
            }
            startActivity(intent)
        }*/
    }
}