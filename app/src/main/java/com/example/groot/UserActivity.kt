package com.example.groot

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import coil.transform.CircleCropTransformation
import com.example.groot.viewmodel.UserViewModel
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import de.hdodenhof.circleimageview.CircleImageView

class UserActivity : AppCompatActivity() {

    private val viewModel: UserViewModel by viewModels()

    private lateinit var viewUsername: TextView
    private lateinit var followersCount: TextView
    private lateinit var followingCount: TextView
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
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val userId = intent.getStringExtra("userId") ?: ""
        viewModel.getUserId(userId)

        viewUsername = findViewById(R.id.viewUsername)
        followersCount = findViewById(R.id.followersCount)
        followingCount = findViewById(R.id.followingCount)
        profileImage = findViewById(R.id.profileImage)
        btnFollow = findViewById(R.id.btnFollow)
        appBar = findViewById(R.id.topAppBar)

        viewModel.profile.observe(this) { user ->
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

        appBar.setNavigationOnClickListener {
            finish()
        }
    }
}