package com.example.groot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.repositories.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()

    val hasUser: Boolean get() =  authRepository.hasUser

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                authRepository.login(email, password)
                _authStatus.value = "Login Successful"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup failed :- ${e.message.toString()}")
                _authStatus.value = e.message.toString()
            }
        }.invokeOnCompletion {
            _isLoading.value = false
        }
    }

    fun signup(email: String, password: String, imgUrl: String, userName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                if (authRepository.checkUsername(userName)) {
                    _authStatus.value = "Username already exists"
                    return@launch
                }
                authRepository.signup(email, password, imgUrl, userName)
                _authStatus.value = "Signup Successful"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup failed :- ${e.message.toString()}")
                _authStatus.value = e.message.toString()
            }
        }.invokeOnCompletion {
            _isLoading.value = false
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authStatus.value = "SignedOut Successfully"
    }
}