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

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FollowingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var viewModel: ProfileViewModel

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
        val view = inflater.inflate(R.layout.fragment_following, container, false)

        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewFollowing)
        val message = view?.findViewById<TextView>(R.id.message)

        recyclerView?.layoutManager = LinearLayoutManager(requireContext())
        val recyclerAdapter = UserListRecyclerViewAdapter(emptyList()) { onItemClick(it) }
        recyclerView?.adapter = recyclerAdapter
        viewModel.followingProfiles.observe(viewLifecycleOwner) { following ->
            message?.isVisible = following.isEmpty()
            recyclerAdapter.updateUsers(following)
            recyclerAdapter.notifyDataSetChanged()
        }

        return view
    }

    private fun onItemClick(userId: String) {
        val intent = Intent(requireContext(), UserActivity::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FollowingFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FollowingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}