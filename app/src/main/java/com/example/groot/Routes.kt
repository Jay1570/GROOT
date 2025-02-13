package com.example.groot

import kotlinx.serialization.Serializable

sealed interface Routes

@Serializable
data object Login : Routes

@Serializable
data object Registration : Routes

@Serializable
data object Home : Routes