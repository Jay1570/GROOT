package com.example.groot

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.groot.screens.auth.LoginScreen
import com.example.groot.screens.auth.RegistrationScreen
import com.example.groot.screens.home.FriendsScreen
import com.example.groot.screens.home.HomeScreen
import com.example.groot.screens.repository.*
import com.example.groot.screens.search.RepoSearchScreen
import com.example.groot.screens.search.UserSearchScreen
import com.example.groot.screens.settings.SettingsScreen
import com.example.groot.screens.user.UserScreen

@Composable
fun Navigation(
    viewModel: NavigationViewModel = viewModel(),
) {
    val startDestination = viewModel.checkUserStatus()
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable<Login> {
            LoginScreen(
                navigateToRegister = {
                    navController.navigate(Registration) {
                        launchSingleTop = true
                    }
                },
                navigateToHome = {
                    navController.navigate(Home) {
                        launchSingleTop = true
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Registration> {
            RegistrationScreen(
                navigateToHome = {
                    navController.navigate(Home) {
                        launchSingleTop = true
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen(
                navControllerMain = navController,
            )
        }

        composable<RepositoryList> {
            RepositoryListScreen(
                navigateBack = { navController.popBackStack() },
                navigateToRepoDetails = {
                    navController.navigate(RepositoryDetails(it)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<RepositoryDetails> {
            RepositoryDetailsScreen(
                navigateBack = { navController.popBackStack() },
                navigateToFileList = {
                    navController.navigate(FileList(it)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<StarredRepoList> {
            StarredRepoListScreen(
                navigateBack = { navController.popBackStack() },
                navigateToRepoDetails = {
                    navController.navigate(RepositoryDetails(it)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<UserSearch> {
            UserSearchScreen(
                navigateBack = { navController.popBackStack() },
                navigateToUser = {
                    navController.navigate(UserScreen(it)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<RepoSearch> {
            RepoSearchScreen(
                navigateBack = { navController.popBackStack() },
                navigateToRepository = {
                    navController.navigate(RepositoryDetails(it)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<UserScreen> {
            UserScreen(
                navigateBack = { navController.popBackStack() },
                navigateToRepoList = {
                    navController.navigate(RepositoryList(it)) {
                        launchSingleTop = true
                    }
                },
                navigateToStarredRepo = {
                    navController.navigate(StarredRepoList(it)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable<Friends> {
            val screen = it.toRoute<Friends>().screen
            FriendsScreen(
                navigateToUser = { id ->
                    navController.navigate(UserScreen(id)) {
                        launchSingleTop = true
                    }
                },
                navigateBack = { navController.popBackStack() },
                screen = screen
            )
        }

        composable<Settings> {
            SettingsScreen(
                navigateBack = { navController.popBackStack() },
                navigateToLogin = {
                    navController.navigate(Login) {
                        launchSingleTop = true
                        popUpTo(0) { inclusive = true }
                    }
                },
            )
        }

        composable<FileList> {
            FileListScreen(
                navigateToFileContent = {
                    navController.navigate(FileContent(it)) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable<FileContent> {
            FileContentScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}