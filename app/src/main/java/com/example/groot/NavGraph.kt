package com.example.groot

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.groot.screens.auth.LoginScreen

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

        }

        composable<Home> {

        }
    }
}