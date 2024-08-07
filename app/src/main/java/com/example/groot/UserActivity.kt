package com.example.groot

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.adapter.UserListRecyclerViewAdapter
import com.example.groot.repositories.UserRepository

class UserActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val userRepository = UserRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        recyclerView = findViewById(R.id.recyclerViewFollowers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val recyclerAdapter = UserListRecyclerViewAdapter(userRepository.followerProfiles.value ?: emptyList())
        recyclerView.adapter = recyclerAdapter
        userRepository.followerProfiles.observe(this) {
            recyclerAdapter.updateUsers(it)
            recyclerAdapter.notifyDataSetChanged()
            Log.d("User",it.size.toString())
        }
    }
}