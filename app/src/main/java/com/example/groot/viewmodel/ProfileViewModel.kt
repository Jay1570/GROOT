package com.example.groot.viewmodel

import androidx.lifecycle.ViewModel
import com.example.groot.repositories.AuthRepository

class ProfileViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepository()

    val profile = authRepository.profile
    val followers = authRepository.followers
    val following = authRepository.following
}