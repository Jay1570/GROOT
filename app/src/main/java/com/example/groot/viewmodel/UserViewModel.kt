package com.example.groot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.model.Friends
import com.example.groot.model.User
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()

    private var userId = ""

    val user: LiveData<User> = userRepository.user
    val userFriends: LiveData<Friends> = userRepository.userFriends

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> get() = _isFollowing

    private val friends = userRepository.friends

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val friendsObserver = Observer<Friends> { friendsData ->
        val isCurrentlyFollowing = friendsData.following.contains(userId)
        _isFollowing.value = isCurrentlyFollowing
    }

    init {
        friends.observeForever(friendsObserver)
    }

    fun getUserId(id: String) {
        userId = id
        userRepository.getProfile(userId)
        userRepository.getUserFriends(userId)
    }

    fun follow() {
        viewModelScope.launch {
            try {
                userRepository.follow(userId)
                _isFollowing.value = true
            } catch (e: Exception) {
                _error.value = e.message.toString()
            }
        }
    }

    fun unfollow() {
        viewModelScope.launch {
            try {
                userRepository.unfollow(userId)
                _isFollowing.value = false
            } catch (e: Exception) {
                _error.value = e.message.toString()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        friends.removeObserver(friendsObserver)
    }
}