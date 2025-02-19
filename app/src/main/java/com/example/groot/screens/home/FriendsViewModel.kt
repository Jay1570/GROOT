package com.example.groot.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.model.User
import com.example.groot.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {

    private val userRepository = UserRepository()

    private val _uiState = MutableStateFlow(FriendsUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.getFriends().collect { friends ->
                userRepository.getFriendsProfiles(friends.followers).collect { followers ->
                    _uiState.update { it.copy(followersProfile = followers) }
                }
                userRepository.getFriendsProfiles(friends.following).collect { following ->
                    _uiState.update { it.copy(followingProfile = following) }
                }
            }
        }
    }
}

data class FriendsUiState(
    val followersProfile: List<User> = emptyList(),
    val followingProfile: List<User> = emptyList()
)