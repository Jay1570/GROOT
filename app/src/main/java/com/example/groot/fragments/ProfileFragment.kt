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
import com.example.groot.R
import com.example.groot.UserActivity
import com.example.groot.viewmodel.ProfileViewModel
import de.hdodenhof.circleimageview.CircleImageView

// TODO: Rename parameter arguments, choose names that match
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
    // TODO: Rename and change types of parameters
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

        viewModel.followers.observe(viewLifecycleOwner) { followers ->
            followerCount?.text = followers.followers.count().toString()
        }

        viewModel.following.observe(viewLifecycleOwner) { following ->
            followingCount?.text = following.following.count().toString()
        }

        followerCount?.setOnClickListener {
            startActivity(Intent(requireContext(), UserActivity::class.java))
        }

        view?.findViewById<TextView>(R.id.followers)?.setOnClickListener {
            startActivity(Intent(requireContext(), UserActivity::class.java))
        }

        return view
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
        // TODO: Rename and change types and number of parameters
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