package com.example.groot.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.R
import com.example.groot.common.ProfileCard
import com.example.groot.common.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateToFriends: (Int) -> Unit,
    navigateToSettings: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Profile",
                canNavigateBack = false,
                actions = {
                    IconButton(onClick = navigateToSettings) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        ProfileContent(
            uiState = uiState,
            onOldPasswordChange = viewModel::onOldPasswordChange,
            onNewPasswordChange = viewModel::onNewPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onPasswordVisibilityChange = viewModel::onPasswordVisibilityChange,
            onUpdateClick = viewModel::updatePassword,
            onFollowersClick = { navigateToFriends(0) },
            onFollowingClick = { navigateToFriends(1) },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                )
        )
    }
}

@Composable
private fun ProfileContent(
    uiState: ProfileUiState,
    onOldPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onFollowersClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onUpdateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val enabled = !uiState.inProcess
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileCard(
                profile = uiState.profile,
                followersCount = uiState.friends.followers.size,
                followingCount = uiState.friends.following.size,
                onFollowersClick = onFollowersClick,
                onFollowingClick = onFollowingClick,
                enabled = enabled
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.oldPassword,
                singleLine = true,
                enabled = enabled,
                onValueChange = { onOldPasswordChange(it) },
                label = { Text(text = stringResource(id = R.string.old_password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Password, contentDescription = "Lock Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.newPassword,
                singleLine = true,
                enabled = enabled,
                onValueChange = { onNewPasswordChange(it) },
                label = { Text(text = stringResource(id = R.string.new_password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Lock Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = uiState.confirmPassword,
                singleLine = true,
                enabled = enabled,
                onValueChange = { onConfirmPasswordChange(it) },
                label = { Text(text = stringResource(id = R.string.conform_password)) },
                visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibilityChange() }) {
                        Icon(
                            imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (uiState.isPasswordVisible) "Hide password " else "Show password"
                        )
                    }
                },
                leadingIcon = {
                    Icon(Icons.Default.Password, contentDescription = "Lock Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onUpdateClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = stringResource(id = R.string.update_password))
            }
        }
        if (uiState.inProcess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}