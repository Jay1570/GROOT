package com.example.groot.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.example.groot.FriendsActivity
import com.example.groot.R
import com.example.groot.utility.isValidPassword
import com.example.groot.viewmodel.ProfileViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
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
        val txtOld = view?.findViewById<EditText>(R.id.txtOld)
        val txtNew = view?.findViewById<EditText>(R.id.txtNew)
        val txtConfirm = view?.findViewById<EditText>(R.id.txtConfirm)
        val btnUpdate = view?.findViewById<MaterialButton>(R.id.btnUpdate)
        val progressBar = view?.findViewById<CircularProgressIndicator>(R.id.progressBar)

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

        viewModel.authStatus.observe(viewLifecycleOwner) { message ->
            showSnackBar(message)
            if (message.contains("Successfully")) {
                txtOld?.setText("")
                txtNew?.setText("")
                txtConfirm?.setText("")
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar?.isVisible = isLoading
            txtOld?.isEnabled = !isLoading
            txtNew?.isEnabled = !isLoading
            txtConfirm?.isEnabled = !isLoading
            btnUpdate?.isEnabled = !isLoading
            followerCount?.isEnabled = !isLoading
            followers?.isEnabled = !isLoading
            followingCount?.isEnabled = !isLoading
            following?.isEnabled = !isLoading
        }

        viewModel.friends.observe(viewLifecycleOwner) { friends ->
            followerCount?.text = friends.followers.count().toString()
            followingCount?.text = friends.following.count().toString()
        }

        btnUpdate?.setOnClickListener {
            val oldPassword = txtOld?.text.toString()
            val newPassword = txtNew?.text.toString()
            val conPassword = txtConfirm?.text.toString()
            if (oldPassword.isEmpty() || newPassword.isEmpty() || conPassword.isEmpty()) {
                showSnackBar(getString(R.string.empty_fields))
                return@setOnClickListener
            }
            if (oldPassword == newPassword) {
                showSnackBar(getString(R.string.same_password))
                return@setOnClickListener
            }
            if (!newPassword.isValidPassword()) {
                showSnackBar(getString(R.string.invalid_password))
                return@setOnClickListener
            }
            if (newPassword != conPassword) {
                showSnackBar(getString(R.string.confirm_password))
                return@setOnClickListener
            }
            viewModel.updatePassword(oldPassword, newPassword)
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

    private fun showSnackBar(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}