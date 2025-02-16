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
data class RepositoryList(
    val username: String
) : Routes

@Serializable
data class Repository(
    val path: String
) : Routes

@Serializable
data object FileList : Routes

@Serializable
data object FileContent : Routes

@Serializable
data class StarredRepoList(
    val userId: String
) : Routes

@Serializable
data class UserSearch(
    val query: String
) : Routes

@Serializable
data class RepoSearch(
    val query: String
) : Routes

@Serializable
data class User(
    val id: String
) : Routes

@Serializable
data class Friends(
    val screen: Int
) : Routes

@Serializable
data object Followers : Routes

@Serializable
data object Following : Routes

@Serializable
data object Settings : Routes