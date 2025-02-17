package com.example.groot.screens.user

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.example.groot.UserScreen
import com.example.groot.repositories.OtherUserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val userRepository = OtherUserRepository()
    private val userId: String = savedStateHandle.toRoute<UserScreen>().id

    private val currentUserId = userRepository.currentUserId

    val profile get() = userRepository.profile
    val friends get() = userRepository.friends

    private val _isFollowing = MutableStateFlow(false)
    val isFollowing get() = _isFollowing.asStateFlow()

    init {
        userRepository.getProfile(userId)
        userRepository.getFriends(userId)
        isFollowing()
    }

    fun onFollowClick() {
        viewModelScope.launch {
            try {
                if (!_isFollowing.value) userRepository.follow(userId)
                else userRepository.unfollow(userId)
            } catch (e: Exception) {
                SnackbarManager.sendEvent(SnackbarEvent(e.message.toString()))
            }
        }
    }

    private fun isFollowing() {
        viewModelScope.launch {
            friends.collectLatest {
                _isFollowing.value = it.followers.contains(currentUserId)
            }
        }
    }
}