package com.example.groot.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.groot.FriendsActivity
import com.example.groot.R
import com.example.groot.viewmodel.ProfileViewModel
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val viewUsername = view?.findViewById<TextView>(R.id.viewUsername)
        val profileImage = view?.findViewById<CircleImageView>(R.id.profileImage)
        val followerCount = view?.findViewById<TextView>(R.id.followersCount)
        val followingCount = view?.findViewById<TextView>(R.id.followingCount)
        val followers = view?.findViewById<TextView>(R.id.followers)
        val following = view?.findViewById<TextView>(R.id.following)

        viewModel.profile.observe(viewLifecycleOwner) { user ->
            viewUsername?.text = user.userName
            if (user.imgUrl.isNotEmpty()) {
                profileImage?.load(user.imgUrl) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.user)
                    error(R.drawable.user)
                }
            }
        }

        viewModel.friends.observe(viewLifecycleOwner) { friends ->
            followerCount?.text = friends.followers.count().toString()
            followingCount?.text = friends.following.count().toString()
        }

        val intent = Intent(requireContext(), FriendsActivity::class.java)

        followerCount?.setOnClickListener {
            startActivity(intent)
        }

        followers?.setOnClickListener {
            startActivity(intent)
        }

        followingCount?.setOnClickListener {
            intent.putExtra("position", 1)
            startActivity(intent)
        }

        following?.setOnClickListener {
            intent.putExtra("position", 1)
            startActivity(intent)
        }

        return view
    }
}