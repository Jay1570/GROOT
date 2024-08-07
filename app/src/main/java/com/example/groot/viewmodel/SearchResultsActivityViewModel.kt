package com.example.groot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.model.User
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.launch

class SearchResultsActivityViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _userList = MutableLiveData<List<User>>(emptyList())
    val userList: LiveData<List<User>> get() = _userList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun onSearch(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val users = userRepository.fetchUsersByNameAndEmail(query)
                _userList.value = users
            } catch (e: Exception) {
                _error.value = "Error fetching users :- ${e.message.toString()}"
            }
        }.invokeOnCompletion {
            _isLoading.value = false
        }
    }
}