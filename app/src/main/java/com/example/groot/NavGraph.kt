package com.example.groot

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groot.screens.auth.LoginScreen
import com.example.groot.screens.auth.RegistrationScreen
import com.example.groot.screens.home.HomeScreen
import com.example.groot.screens.repository.FileContentScreen
import com.example.groot.screens.repository.FileListScreen
import com.example.groot.screens.repository.RepositoryDetailsScreen
import com.example.groot.screens.repository.RepositoryListScreen
import com.example.groot.screens.repository.StarredRepoListScreen
import com.example.groot.screens.search.RepoSearchScreen
import com.example.groot.screens.search.UserSearchScreen

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
                    navController.navigate(User(it)) {
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

        composable<User> {

        }

        composable<Friends> {

        }

        composable<Settings> {

        }

        composable<FileList> {
            FileListScreen(
                navigateToFileContent = {
                    navController.navigate(FileContent(it)) {
                        launchSingleTop = true
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