package com.example.groot.fragments

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.groot.R
import com.example.groot.FriendsActivity
import com.example.groot.SearchResultsActivity
import com.example.groot.Settings
import com.example.groot.viewmodel.ProfileViewModel
import com.google.android.material.appbar.MaterialToolbar
import de.hdodenhof.circleimageview.CircleImageView

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
            if (user.imgUrl.isNotEmpty()){
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

        followerCount?.setOnClickListener {
            startActivity(Intent(requireContext(), FriendsActivity::class.java))
        }

        followers?.setOnClickListener {
            startActivity(Intent(requireContext(), FriendsActivity::class.java))
        }

        followingCount?.setOnClickListener {
            val intent = Intent(Intent(requireContext(), FriendsActivity::class.java))
            intent.putExtra("position", 1)
            startActivity(intent)
        }

        following?.setOnClickListener {
            val intent = Intent(Intent(requireContext(), FriendsActivity::class.java))
            intent.putExtra("position", 1)
            startActivity(intent)
        }

        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val appBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        appBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.Setting -> {
                    startActivity(Intent(requireContext(),Settings::class.java))
                    true
                }
                else -> false
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}