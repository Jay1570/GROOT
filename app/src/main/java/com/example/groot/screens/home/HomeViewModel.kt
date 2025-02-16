package com.example.groot.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    init {
        loadUsername()
    }

    fun loadUsername() {
        viewModelScope.launch {
            _username.value = userRepository.getUsername()
        }
    }

    fun getUserId(): String {
        return userRepository.getUserId()
    }
}