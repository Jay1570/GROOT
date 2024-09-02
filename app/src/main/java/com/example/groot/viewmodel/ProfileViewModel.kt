package com.example.groot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.groot.repositories.UserRepository

class ProfileViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()

    val profile = userRepository.profile.asLiveData()
    val friends = userRepository.friends.asLiveData()
    val followingProfiles = userRepository.followingProfiles.asLiveData()
    val followerProfiles = userRepository.followerProfiles.asLiveData()

    init {
        userRepository.getProfile()
        userRepository.getFriends()
    }
}