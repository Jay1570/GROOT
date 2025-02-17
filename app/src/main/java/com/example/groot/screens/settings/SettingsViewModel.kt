package com.example.groot.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.example.groot.Theme
import com.example.groot.ThemePreference
import com.example.groot.repositories.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(private val themePreference: ThemePreference) : ViewModel() {

    private val authRepository = AuthRepository()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            themePreference.theme.collectLatest { theme ->
                _uiState.update { it.copy(currentTheme = theme) }
            }
        }
    }

    fun signOut(navigateToLogin: () -> Unit) {
        authRepository.signOut()
        navigateToLogin()
        viewModelScope.launch {
            SnackbarManager.sendEvent(SnackbarEvent("SignedOut Successfully"))
        }
    }

    fun onThemeClick() {
        _uiState.update { it.copy(isThemeDialogVisible = true) }
    }

    fun onDismissThemeDialog() {
        _uiState.update { it.copy(isThemeDialogVisible = false) }
    }

    fun onThemeSelected(theme: Theme) {
        viewModelScope.launch {
            themePreference.setTheme(theme)
        }
    }

}

data class SettingsUiState(
    val isThemeDialogVisible: Boolean = false,
    val currentTheme: Theme = Theme.SYSTEM,
)
