package com.example.groot

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groot.screens.auth.LoginScreen
import com.example.groot.screens.auth.RegistrationScreen
import com.example.groot.screens.home.HomeScreen
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

        }

        composable<Repository> {

        }

        composable<StarredRepoList> {

        }

        composable<UserSearch> {
            UserSearchScreen(
                navigateBack = { navController.popBackStack() },
                navigateToUser = {}
            )
        }

        composable<RepoSearch> {
            RepoSearchScreen(
                navigateBack = { navController.popBackStack() },
                navigateToRepository = {
                    navController.navigate(Repository(it)) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}