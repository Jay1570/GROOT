package com.example.groot.screens.user

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.AppViewModelProvider
import com.example.groot.R
import com.example.groot.common.ProfileCard
import com.example.groot.common.TopBar
import com.example.groot.model.Friends
import com.example.groot.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    navigateToRepoList: (String) -> Unit,
    navigateToStarredRepo: (String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: UserViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val isFollowing by viewModel.isFollowing.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar(
                title = profile.userName,
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        UserScreenContent(
            profile = profile,
            friends = friends,
            isFollowing = isFollowing,
            onFollowClick = viewModel::onFollowClick,
            navigateToRepoList = navigateToRepoList,
            navigateToStarredRepo = navigateToStarredRepo,
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        )
    }
}

@Composable
private fun UserScreenContent(
    profile: User,
    friends: Friends,
    isFollowing: Boolean,
    onFollowClick: () -> Unit,
    navigateToRepoList: (String) -> Unit,
    navigateToStarredRepo: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileCard(
                profile = profile,
                followersCount = friends.followers.size,
                followingCount = friends.following.size,
                onFollowersClick = {},
                onFollowingClick = {}
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onFollowClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceContainer, contentColor = MaterialTheme.colorScheme.onSurface)) {
                Text(if (isFollowing) "Unfollow" else "Follow")
            }

            Spacer(Modifier.height(16.dp))

            TextButton(
                onClick = { navigateToRepoList(profile.userName) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.repository),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(id = R.string.Repositories),
                    modifier = Modifier.weight(1f)
                )
            }
            TextButton(
                onClick = { navigateToStarredRepo(profile.userId) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.starred),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                Text(
                    text = stringResource(id = R.string.Starred),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
