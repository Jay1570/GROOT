package com.example.groot.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.example.groot.repositories.AuthRepository
import com.example.groot.utility.isValidEmail
import com.example.groot.utility.isValidPassword
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    private val email get() = uiState.value.email
    private val password get() = uiState.value.password
    private val isPasswordVisible get() = uiState.value.isPasswordVisible
    private val inProcess get() = uiState.value.inProcess

    private val authRepository: AuthRepository = AuthRepository()

    fun onEmailChange(newValue: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(email = newValue)
            }
        }
    }

    fun onPasswordChange(newValue: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(password = newValue)
            }
        }
    }

    fun onPasswordVisibilityChange() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isPasswordVisible = !isPasswordVisible)
            }
        }
    }

    fun onLoginClick(navigateToHome: () -> Unit) {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty()) {
                SnackbarManager.sendEvent(SnackbarEvent("Every Field is mandatory"))
                return@launch
            }

            if (!email.isValidEmail()) {
                SnackbarManager.sendEvent(SnackbarEvent("Invalid Email!"))
                return@launch
            }
            _uiState.value = _uiState.value.copy(inProcess = true)
            if (!password.isValidPassword()) {
                SnackbarManager.sendEvent(SnackbarEvent("Password must include at least 1 uppercase, 1 lowercase, 1 digit and 8 characters."))
                return@launch
            }

            try {
                authRepository.login(email, password)
                SnackbarManager.sendEvent(SnackbarEvent("Login Successful"))
                navigateToHome()
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup failed :- ${e.message.toString()}")
                SnackbarManager.sendEvent(SnackbarEvent(e.message.toString()))
            }
        }.invokeOnCompletion {
            _uiState.value = _uiState.value.copy(inProcess = false)
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val inProcess: Boolean = false
)