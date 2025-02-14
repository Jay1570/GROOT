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

class RegistrationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState = _uiState.asStateFlow()

    private val username get() = _uiState.value.username
    private val email get() = _uiState.value.email
    private val password get() = _uiState.value.password
    private val confirmPassword get() = _uiState.value.confirmPassword
    private val isPasswordVisible get() = _uiState.value.isPasswordVisible

    private val authRepository: AuthRepository = AuthRepository()

    fun onUsernameChange(newValue: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(username = newValue)
            }
        }
    }

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

    fun onConfirmPasswordChange(newValue: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(confirmPassword = newValue)
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

    fun signup(navigateToHome: () -> Unit) {
        viewModelScope.launch {
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || username.isEmpty()) {
                SnackbarManager.sendEvent(SnackbarEvent("Every Field is mandatory"))
                return@launch
            }

            if (!email.isValidEmail()) {
                SnackbarManager.sendEvent(SnackbarEvent("Invalid Email!"))
                return@launch
            }
            if (!password.isValidPassword()) {
                SnackbarManager.sendEvent(SnackbarEvent("Password must include at least 1 uppercase, 1 lowercase, 1 digit and 8 characters."))
                return@launch
            }
            if (password != confirmPassword) {
                SnackbarManager.sendEvent(SnackbarEvent("Confirm password must be same as password"))
                return@launch
            }

            _uiState.value = _uiState.value.copy(inProcess = true)
            try {
                if (authRepository.checkUsername(username)) {
                    SnackbarManager.sendEvent(SnackbarEvent("Username already exists"))
                    return@launch
                }
                authRepository.signup(email, password, "", username)
                SnackbarManager.sendEvent(SnackbarEvent("Signup Successful"))
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

data class RegistrationUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val inProcess: Boolean = false
)