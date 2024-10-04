package com.example.groot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.groot.repositories.AuthRepository
import com.example.groot.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()
    private val authRepository = AuthRepository()

    val profile = userRepository.profile.asLiveData()
    val friends = userRepository.friends.asLiveData()
    val followingProfiles = userRepository.followingProfiles.asLiveData()
    val followerProfiles = userRepository.followerProfiles.asLiveData()

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        userRepository.getProfile()
        userRepository.getFriends()
    }

    fun updatePassword(oldPass: String, newPass: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (authRepository.updatePassword(oldPass, newPass)) {
                    _authStatus.value = "Password Changed Successfully"
                } else {
                    _authStatus.value = "Error Updating Password"
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authStatus.value = "Old Password is incorrect"
            }
        }.invokeOnCompletion {
            _isLoading.value = false
        }
    }

}