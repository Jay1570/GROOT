package com.example.groot

import kotlinx.serialization.Serializable

sealed interface Routes

@Serializable
data object Login : Routes

@Serializable
data object Registration : Routes

@Serializable
data object Home : Routes

@Serializable
data object HomeScreen : Routes

@Serializable
data object ExploreScreen : Routes

@Serializable
data object ProfileScreen : Routes

@Serializable
data object RepositoryList : Routes

@Serializable
data object StarredRepoList: Routes