package com.example.groot.screens.repository

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.RepositoryList
import com.example.groot.model.Repository
import com.example.groot.repositories.RepositoryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class RepositoryListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val repository = RepositoryData()

    private val _repoList = MutableStateFlow<List<Repository>>(emptyList())
    val repoList get() = _repoList.asStateFlow()

    private var username: String = ""

    private val _inProcess = MutableStateFlow(false)
    val inProcess get() = _inProcess.asStateFlow()

    init {
        username = savedStateHandle.toRoute<RepositoryList>().username
        fetchRepositories(username)
    }

    private fun fetchRepositories(username: String = this.username) {
        Log.d("RepositoryListViewModel", "Fetching repositories for user: $username")
        viewModelScope.launch {
            _inProcess.value = true
            repository.fetchRepositories(username)
                .catch { e -> Log.e("RepositoryListViewModel", e.message.toString()) }
                .collect { repos ->
                    _repoList.value = repos
                    _inProcess.value = false
                }
        }
    }
}