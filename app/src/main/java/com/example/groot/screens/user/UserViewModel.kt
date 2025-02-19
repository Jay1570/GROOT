package com.example.groot.screens.user

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.example.groot.UserScreen
import com.example.groot.model.Friends
import com.example.groot.model.User
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class UserViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val userRepository = UserRepository()
    private val userId: String = savedStateHandle.toRoute<UserScreen>().id

    private val currentUserId = userRepository.currentUserId

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState get() = _uiState.asStateFlow()

    private val isFollowing get() = _uiState.value.isFollowing

    init {
        fetchProfile()
        fetchFriends()
    }

    fun onFollowClick() {
        viewModelScope.launch {
            try {
                if (!isFollowing) userRepository.follow(userId)
                else userRepository.unfollow(userId)
            } catch (e: Exception) {
                SnackbarManager.sendEvent(SnackbarEvent(e.message.toString()))
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            userRepository.getProfile(userId)
                .catch { e -> Log.e("ProfileViewModel", e.message.toString()) }
                .collectLatest { user ->
                    _uiState.update { it.copy(profile = user) }
                }
        }
    }

    private fun fetchFriends() {
        viewModelScope.launch {
            userRepository.getFriends(userId)
                .catch { e -> Log.e("ProfileViewModel", e.message.toString()) }
                .collectLatest { friends ->
                    _uiState.update { it.copy(friends = friends, isFollowing = friends.followers.contains(currentUserId)) }
                }
        }
    }
}

data class UserUiState(
    val profile: User = User(),
    val friends: Friends = Friends(),
    val isFollowing: Boolean = false
)