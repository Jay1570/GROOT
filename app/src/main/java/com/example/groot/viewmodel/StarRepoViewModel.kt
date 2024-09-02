package com.example.groot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.groot.repositories.UserRepository

class StarRepoViewModel : ViewModel() {

    private val userRepository = UserRepository()

    val starredRepositories = userRepository.starredRepositories.asLiveData()

    fun getStarRepo() {
        userRepository.getStarredRepositories()
    }
}