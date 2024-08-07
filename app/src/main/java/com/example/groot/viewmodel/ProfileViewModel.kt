package com.example.groot.viewmodel

import androidx.lifecycle.ViewModel
import com.example.groot.repositories.UserRepository

class ProfileViewModel : ViewModel() {

    private val authRepository: UserRepository = UserRepository()

    val profile = authRepository.profile
    val friends = authRepository.friends
}