package com.example.groot.viewmodel

import androidx.lifecycle.ViewModel
import com.example.groot.repositories.UserRepository

class ProfileViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()

    val profile = userRepository.profile
    val friends = userRepository.friends
    val followingProfiles = userRepository.followingProfiles
    val followerProfiles = userRepository.followerProfiles
}