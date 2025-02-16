package com.example.groot.screens.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.groot.ExploreScreen
import com.example.groot.Friends
import com.example.groot.HomeScreen
import com.example.groot.ProfileScreen
import com.example.groot.R
import com.example.groot.RepoSearch
import com.example.groot.RepositoryDetails
import com.example.groot.RepositoryList
import com.example.groot.Routes
import com.example.groot.Settings
import com.example.groot.StarredRepoList
import com.example.groot.UserSearch

@Composable
fun HomeScreen(
    navControllerMain: NavHostController,
    navControllerBottomBar: NavHostController = rememberNavController(),
    viewModel: HomeViewModel = viewModel()
) {
    val items = listOf(
        NavigationItem(stringResource(R.string.home), HomeScreen, R.drawable.home_outlined, R.drawable.home_filled),
        NavigationItem(stringResource(R.string.explore), ExploreScreen, R.drawable.explore_outlined, R.drawable.explore_filled),
        NavigationItem(stringResource(R.string.profile), ProfileScreen, R.drawable.profile_outline, R.drawable.profile_filled)
    )
    Scaffold(
        bottomBar = {
            BottomAppBar {
                val navBackStackEntry by navControllerBottomBar.currentBackStackEntryAsState()
                val current = navBackStackEntry?.destination
                items.forEach { route ->
                    val selected = current?.hierarchy?.any { it.hasRoute(route.route::class) } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = if (selected) { painterResource(route.selectedIcon) } else { painterResource(route.icon) },
                                contentDescription = route.title
                            )
                        },
                        selected = selected,
                        onClick = {
                            navControllerBottomBar.navigate(route.route) {
                                popUpTo(navControllerBottomBar.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navControllerBottomBar,
            startDestination = HomeScreen,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable<HomeScreen> {
                HomeContent(
                    navigateToRepoList = {
                        navControllerMain.navigate(RepositoryList(viewModel.username.value)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToStarredList = {
                        navControllerMain.navigate(StarredRepoList(viewModel.getUserId())) {
                            launchSingleTop = true
                        }
                    },
                    navigateToRepoSearch = {
                        navControllerMain.navigate(RepoSearch(it)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToUserSearch = {
                        navControllerMain.navigate(UserSearch(it)) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<ExploreScreen> {
                ExploreScreen(
                    navigateToRepository = {
                        navControllerMain.navigate(RepositoryDetails(it)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToUserSearch = {
                        navControllerMain.navigate(UserSearch(it)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToRepoSearch = {
                        navControllerMain.navigate(RepoSearch(it)) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable<ProfileScreen> {
                ProfileScreen(
                    navigateToFriends = {
                        navControllerMain.navigate(Friends(it)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToSettings = {
                        navControllerMain.navigate(Settings) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: Routes,
    @DrawableRes val icon: Int,
    @DrawableRes val selectedIcon: Int,
)