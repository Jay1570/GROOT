package com.example.groot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.model.Repository
import com.example.groot.model.User
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.launch

class SearchResultsActivityViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _userList = MutableLiveData<List<User>>(emptyList())
    val userList: LiveData<List<User>> get() = _userList

    private val _repoList = MutableLiveData<List<Repository>>(emptyList())
    val repoList: LiveData<List<Repository>> get() = _repoList

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun onUserSearch(query: String) {
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

    fun onRepositorySearch(query: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val repository = userRepository.searchRepository(query)
                _repoList.value = repository
            } catch (e: Exception) {
                _error.value = "Error fetching repositories :- ${e.message.toString()}"
                Log.e("Error", e.message.toString())
            }
        }.invokeOnCompletion {
            _isLoading.value = false
        }
    }
}