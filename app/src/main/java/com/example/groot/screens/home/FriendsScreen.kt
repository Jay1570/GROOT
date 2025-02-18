package com.example.groot.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.R
import com.example.groot.common.TopBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    screen: Int = 0,
    navigateToUser: (String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val tabTitles = listOf(stringResource(R.string.followers), stringResource(R.string.following))
    val pagerState = rememberPagerState(initialPage = screen) { tabTitles.size }

    val followingProfiles = viewModel.followingProfiles.collectAsStateWithLifecycle()
    val followersProfiles = viewModel.followerProfiles.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Column {
                TopBar(
                    title = stringResource(R.string.friends),
                    canNavigateBack = true,
                    navigateUp = navigateBack
                )
                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = { Text(title) }
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { page ->
            when (page) {
                0 -> UserListScreen(followersProfiles.value, navigateToUser)
                1 -> UserListScreen(followingProfiles.value, navigateToUser)
            }
        }
    }
}