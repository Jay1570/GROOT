package com.example.groot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.groot.repositories.RepositoryData

class StarRepoViewModel : ViewModel() {

    private val repository = RepositoryData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    val starredRepositories = repository.starredRepositories.asLiveData()

    fun getStarRepo() {
        _isLoading.value = true
        repository.getStarredRepositories()
        _isLoading.value = false
    }

    fun getStarRepo(userId: String) {
        _isLoading.value = true
        repository.getStarredRepositories(userId)
        _isLoading.value = false
    }
}