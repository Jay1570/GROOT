package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import coil.load
import coil.transform.CircleCropTransformation
import com.example.groot.viewmodel.UserViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView

class UserActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels()

    private lateinit var viewUsername: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount: TextView
    private lateinit var btnRepo: MaterialButton
    private lateinit var btnStarred: MaterialButton
    private lateinit var profileImage: CircleImageView
    private lateinit var btnFollow: AppCompatToggleButton
    private lateinit var appBar: MaterialToolbar
    private var isChecked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)

        window.statusBarColor = getColor(R.color.md_theme_surfaceContainer)

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
        val userId = intent.getStringExtra("userId") ?: ""
        viewModel.getUserId(userId)

        var username = ""

        viewUsername = findViewById(R.id.viewUsername)
        followersCount = findViewById(R.id.followersCount)
        followingCount = findViewById(R.id.followingCount)
        btnRepo = findViewById(R.id.btnRepository)
        btnStarred = findViewById(R.id.btnStarred)
        profileImage = findViewById(R.id.profileImage)
        btnFollow = findViewById(R.id.btnFollow)
        appBar = findViewById(R.id.topAppBar)

        viewModel.profile.observe(this) { user ->
            username = user.userName + " "

            viewUsername.text = user.userName
            if (user.imgUrl.isNotEmpty()){
                profileImage.load(user.imgUrl) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.user)
                    error(R.drawable.user)
                }
            }
        }

        viewModel.friends.observe(this) { friends ->
            followingCount.text = friends.following.size.toString()
            followersCount.text = friends.followers.size.toString()
        }

        viewModel.error.observe(this) { error ->
            Snackbar.make(findViewById(R.id.main), error, Snackbar.LENGTH_SHORT).show()
        }

        viewModel.isFollowing.observe(this) { isFollowing ->
            isChecked = isFollowing
            btnFollow.isChecked = isChecked
        }

        btnFollow.setOnClickListener {
            isChecked = !isChecked
            btnFollow.isChecked = isChecked

            if (isChecked) {
                viewModel.follow()
            } else {
                viewModel.unfollow()
            }
        }

        btnRepo.setOnClickListener {
            val intent = Intent(this, RepoActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        btnStarred.setOnClickListener {
            val intent = Intent(this, StarredActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        appBar.setNavigationOnClickListener {
            finish()
        }
    }
}