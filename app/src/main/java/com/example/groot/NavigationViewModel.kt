package com.example.groot

import androidx.lifecycle.ViewModel
import com.example.groot.repositories.AuthRepository

class NavigationViewModel : ViewModel() {


    private val authRepository: AuthRepository = AuthRepository()

    fun checkUserStatus() : Routes {
        return if (authRepository.hasUser) {
            Home
        } else {
            Login
        }
    }
}