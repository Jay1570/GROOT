package com.example.groot.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.repositories.RepositoryData
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.launch

class ExploreViewModel : ViewModel() {

    private val userRepository = UserRepository()
    private val repository = RepositoryData()

    private val friends get() = userRepository.friends

    val exploreRepository get() = repository.exploreRepositories

    init {
        userRepository.getFriends()
    }

    fun fetchRepo() {
        viewModelScope.launch {
            friends.collect {
                repository.fetchExplorerRepositories(it)
            }
        }
    }
}