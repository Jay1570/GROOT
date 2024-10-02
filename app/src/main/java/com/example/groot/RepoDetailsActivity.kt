package com.example.groot

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.example.groot.viewmodel.RepoDetailsViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.snackbar.Snackbar
import io.noties.markwon.Markwon
import io.noties.markwon.image.ImagesPlugin

class RepoDetailsActivity : AppCompatActivity() {

    private val viewModel: RepoDetailsViewModel by viewModels()

    private lateinit var txtUsername: TextView
    private lateinit var txtReponame: TextView
    private lateinit var readmeTextView: TextView
    private lateinit var btnStar: MaterialButton
    private lateinit var starCount: MaterialButton
    private lateinit var progressBar: ProgressBar
    private lateinit var btnCode: MaterialCardView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var maincontent: ScrollView
    private lateinit var languageBarChartView: LanguageBarChartView

    private var isStarred = false
    private lateinit var markwon: Markwon
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_repo_details)

        txtUsername = findViewById(R.id.txt_username)
        txtReponame = findViewById(R.id.txt_repo_name)
        readmeTextView = findViewById(R.id.readme_text)
        btnStar = findViewById(R.id.btnStar)
        starCount = findViewById(R.id.starCount)
        progressBar = findViewById(R.id.progressBar)
        btnCode = findViewById(R.id.btn_code)
        toolbar = findViewById(R.id.topAppBar)
        maincontent = findViewById(R.id.maincontent)
        languageBarChartView = findViewById(R.id.languageBarChartView)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        path = intent.getStringExtra("path") ?: ""

        viewModel.fetchReadmeAndContributions(path)

        val username = path.substringBefore("/").trim()
        val repoName = path.substringAfter("/").trim()

        toolbar.title = repoName
        txtUsername.text = username
        txtReponame.text = repoName

        markwon = Markwon.builder(this).usePlugin(ImagesPlugin.create()).build()

        viewModel.languageContributions.observe(this) { contributions ->
            Log.d("repo_details",contributions.size.toString())
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

        viewModel.starCount.observe(this) { stars ->
            starCount.text = "$stars"
        }

        viewModel.isStarred.observe(this) {
            isStarred = it
            btnStar.text = if (isStarred) getString(R.string.Starred) else getString(R.string.star)
            btnStar.icon = if (isStarred) AppCompatResources.getDrawable(this, R.drawable.filled_star) else AppCompatResources.getDrawable(this, R.drawable.starred)
        }

        btnStar.setOnClickListener {
            if (isStarred) {
                viewModel.unstarRepo(path)
            } else {
                viewModel.starRepo(path)
            }
        }

        btnCode.setOnClickListener {
            val intent = Intent(this, Files::class.java).apply {
                putExtra("path", path)
            }
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets.getInsets(WindowInsetsCompat.Type.ime())
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
    }
}