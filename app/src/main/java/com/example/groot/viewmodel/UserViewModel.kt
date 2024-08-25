package com.example.groot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.groot.model.Friends
import com.example.groot.model.User
import com.example.groot.repositories.OtherUserRepository
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val otherUserRepository: OtherUserRepository = OtherUserRepository()

    private var userId = ""

    val profile: LiveData<User> = otherUserRepository.profile.asLiveData()
    val friends: LiveData<Friends> = otherUserRepository.friends.asLiveData()

    private val _isFollowing = MutableLiveData<Boolean>()
    val isFollowing: LiveData<Boolean> get() = _isFollowing

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val friendsObserver = Observer<Friends> { friendsData ->
        val isCurrentlyFollowing = friendsData.followers.contains(otherUserRepository.currentUserId)
        _isFollowing.value = isCurrentlyFollowing
    }

    init {
        friends.observeForever(friendsObserver)
    }

    fun getUserId(id: String) {
        userId = id
        otherUserRepository.getProfile(userId)
        otherUserRepository.getFriends(userId)
    }

    fun follow() {
        viewModelScope.launch {
            try {
                otherUserRepository.follow(userId)
                _isFollowing.value = true
            } catch (e: Exception) {
                _error.value = e.message.toString()
            }
        }
    }

    fun unfollow() {
        viewModelScope.launch {
            try {
                otherUserRepository.unfollow(userId)
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