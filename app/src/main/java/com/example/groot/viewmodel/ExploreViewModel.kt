package com.example.groot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.groot.repositories.RepositoryData
import com.example.groot.repositories.UserRepository

class ExploreViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val repository = RepositoryData()
    private val friends = userRepository.friends.asLiveData()

    val exploreRepository = repository.exploreRepositories.asLiveData()

    init {
        userRepository.getFriends()
    }

    fun fetchRepo() {
        friends.observeForever {
            repository.fetchExplorerRepositories(it)
        }
    }
}