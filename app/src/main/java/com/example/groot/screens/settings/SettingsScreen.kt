package com.example.groot.screens.settings

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Contrast
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.AppViewModelProvider
import com.example.groot.R
import com.example.groot.Theme
import com.example.groot.common.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navigateBack: () -> Unit,
    navigateToLogin: () -> Unit,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val items = listOf(
        SettingsItem(
            Icons.Default.Share,
            "Invite Your Friends",
            onClick = { shareInvite(context) }
        ),
        SettingsItem(
            Icons.Default.Contrast,
            "Theme",
            onClick = viewModel::onThemeClick
        )
    )


    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(id = R.string.settings),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        SettingsScreenContent(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            itemsList = items,
            onSignOutClick = { viewModel.signOut(navigateToLogin) },
        )
        if (uiState.isThemeDialogVisible) {
            ThemeSelectionDialog(
                currentTheme = uiState.currentTheme,
                onDismiss = viewModel::onDismissThemeDialog,
                onThemeSelected = viewModel::onThemeSelected
            )
        }

    }
}

@Composable
private fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    itemsList: List<SettingsItem>,
    onSignOutClick: () -> Unit,
) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            itemsList.forEach { item ->
                SettingsItemCard(
                    icon = item.icon,
                    title = item.title,
                    onClick = item.onClick
                )
            }
            Button(
                onClick = onSignOutClick,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Sign Out")
            }
        }
    }
}


@Composable
fun SettingsItemCard(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun ThemeSelectionDialog(
    currentTheme: Theme,
    onDismiss: () -> Unit,
    onThemeSelected: (Theme) -> Unit
) {

    val options = listOf(Theme.LIGHT, Theme.DARK, Theme.SYSTEM)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(currentTheme) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Select Theme") },
        text = {
            Column {
                options.forEach { theme ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(theme) }
                    ) {
                        RadioButton(
                            selected = (theme == selectedOption),
                            onClick = { onOptionSelected(theme) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = theme.toString())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onThemeSelected(selectedOption)
                    onDismiss()
                }
            ) {
                Text(text = "Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

private fun shareInvite(context: Context) {
    val downloadLink = "https://github.com/Jay1570/GROOT"
    val invitationMessage = context.getString(R.string.invitation_message, downloadLink)
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, invitationMessage)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.invitation_message)))
}


@Immutable
private data class SettingsItem(
    val icon: ImageVector,
    val title: String,
    val onClick: () -> Unit
)
