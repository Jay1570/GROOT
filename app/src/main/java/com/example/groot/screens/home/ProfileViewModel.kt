package com.example.groot.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.example.groot.model.Friends
import com.example.groot.model.User
import com.example.groot.repositories.AuthRepository
import com.example.groot.repositories.UserRepository
import com.example.groot.utility.isValidPassword
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val userRepository: UserRepository = UserRepository()
    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState get() = _uiState.asStateFlow()

    private val oldPassword get() = _uiState.value.oldPassword
    private val newPassword get() = _uiState.value.newPassword
    private val confirmPassword get() = _uiState.value.confirmPassword

    init {
        fetchProfile()
        fetchFriends()
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            userRepository.getProfile()
                .catch { e -> Log.e("ProfileViewModel", e.message.toString()) }
                .collectLatest { user ->
                    _uiState.update { it.copy(profile = user) }
                }
        }
    }

    private fun fetchFriends() {
        viewModelScope.launch {
            userRepository.getFriends()
                .catch { e -> Log.e("ProfileViewModel", e.message.toString()) }
                .collectLatest { friends ->
                    _uiState.update { it.copy(friends = friends) }
                }
        }
    }

    fun onOldPasswordChange(newValue: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(oldPassword = newValue)
            }
        }
    }

    fun onNewPasswordChange(newValue: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(newPassword = newValue)
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
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun updatePassword() {
        viewModelScope.launch {
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                SnackbarManager.sendEvent(SnackbarEvent("Every Field is mandatory"))
                return@launch
            }
            if (oldPassword == newPassword) {
                SnackbarManager.sendEvent(SnackbarEvent("Old and New Password cannot be same"))
                return@launch
            }
            if (!newPassword.isValidPassword()) {
                SnackbarManager.sendEvent(SnackbarEvent("Password must include at least 1 uppercase, 1 lowercase, 1 digit and 8 characters."))
                return@launch
            }
            if (newPassword != confirmPassword) {
                SnackbarManager.sendEvent(SnackbarEvent("New Password and Confirm Password must be same"))
                return@launch
            }
            _uiState.value = _uiState.value.copy(inProcess = true)
            try {
                if (authRepository.updatePassword(oldPassword, newPassword)) {
                    SnackbarManager.sendEvent(SnackbarEvent("Password Changed Successfully"))
                } else {
                    SnackbarManager.sendEvent(SnackbarEvent("Error Updating Password"))
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                SnackbarManager.sendEvent(SnackbarEvent("Old Password is incorrect"))
            }
        }.invokeOnCompletion {
            _uiState.value = _uiState.value.copy(inProcess = false)
        }
    }
}

data class ProfileUiState(
    val profile: User = User(),
    val friends: Friends = Friends(),
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val inProcess: Boolean = false
)