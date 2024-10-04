package com.example.groot.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.groot.R
import com.example.groot.UserActivity
import com.example.groot.adapter.UserListRecyclerViewAdapter
import com.example.groot.viewmodel.ProfileViewModel

class FollowersFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_followers, container, false)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewFollowers)
        val message = view?.findViewById<TextView>(R.id.message)

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val recyclerAdapter = UserListRecyclerViewAdapter(emptyList()) { onItemClick(it) }
        recyclerView?.adapter = recyclerAdapter
        viewModel.followerProfiles.observe(viewLifecycleOwner) { followers ->
            message?.isVisible = followers.isEmpty()
            recyclerAdapter.updateUsers(followers)
            recyclerAdapter.notifyDataSetChanged()
        }

        return view
    }

    private fun onItemClick(userId: String) {
        val intent = Intent(requireContext(), UserActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }
}