package com.example.groot.screens.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.RepoSearch
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.example.groot.model.Repository
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RepoSearchViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val query = savedStateHandle.toRoute<RepoSearch>().query

    private val _repoList = MutableStateFlow<List<Repository>>(emptyList())
    val repoList = _repoList.asStateFlow()

    private val _inProcess = MutableStateFlow(false)
    val inProcess = _inProcess.asStateFlow()

    private val userRepository = UserRepository()

    init {
        search(query)
    }

    fun search(query: String) {
        _inProcess.value = true
        viewModelScope.launch {
            try {
                val repository = userRepository.searchRepository(query)
                _repoList.value = repository
            } catch (e: Exception) {
                SnackbarManager.sendEvent(SnackbarEvent("Error Fetching Repository :- ${e.message.toString()}"))
                Log.e("Error", e.message.toString())
            }
        }.invokeOnCompletion {
            _inProcess.value = false
        }
    }
}