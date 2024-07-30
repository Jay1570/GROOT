package com.example.groot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.repositories.AuthRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AuthViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()

    val hasUser: Boolean get() =  authRepository.hasUser

    val profile = authRepository.profile

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                authRepository.login(email, password)
                _authStatus.value = "Login Successful"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup failed :- ${e.message.toString()}")
                _authStatus.value = e.message.toString()
            }
        }
    }

    fun signup(email: String, password: String, imgUrl: String, userName: String) {
        viewModelScope.launch {
            try {
                authRepository.signup(email, password, imgUrl, userName)
                _authStatus.value = "Signup Successful"
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup failed :- ${e.message.toString()}")
                _authStatus.value = e.message.toString()
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _authStatus.value = "SignedOut Successfully"
    }

    fun checkUsername(userName: String): Boolean {
        return runBlocking { authRepository.checkUsername(userName) }
    }
}