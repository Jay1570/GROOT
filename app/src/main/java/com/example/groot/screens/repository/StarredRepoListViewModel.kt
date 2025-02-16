package com.example.groot.screens.repository

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.StarredRepoList
import com.example.groot.model.StarredRepositories
import com.example.groot.repositories.RepositoryData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class StarredRepoListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val repository = RepositoryData()

    private val _starredRepo = MutableStateFlow(StarredRepositories())
    val starredRepo get() = _starredRepo.asStateFlow()

    private var userId: String = ""

    private val _inProcess = MutableStateFlow(false)
    val inProcess get() = _inProcess.asStateFlow()

    init {
        userId = savedStateHandle.toRoute<StarredRepoList>().userId
        fetchStarredRepositories(userId)
    }

    private fun fetchStarredRepositories(userId: String = this.userId) {
        viewModelScope.launch {
            _inProcess.value = true
            repository.getStarredRepositories(userId)
                .catch { e -> Log.e("StarredRepoListViewModel", e.message.toString()) }
                .collect { repos ->
                    _starredRepo.value = repos
                    _inProcess.value = false
                }
        }
    }
}