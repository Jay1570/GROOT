package com.example.groot.screens.search

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.RepoSearch
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.example.groot.model.User
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserSearchViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {


    private val query = savedStateHandle.toRoute<RepoSearch>().query

    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList = _userList.asStateFlow()

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
                val users = userRepository.fetchUsersByNameAndEmail(query)
                _userList.value = users
            } catch (e: Exception) {
                SnackbarManager.sendEvent(SnackbarEvent("Error Fetching Users :- ${e.message.toString()}"))
                Log.e("Error", e.message.toString())
            }
        }.invokeOnCompletion {
            _inProcess.value = false
        }
    }
}