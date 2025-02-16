package com.example.groot.screens.repository

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.AppViewModelProvider
import com.example.groot.R
import com.example.groot.common.RepositoryItem
import com.example.groot.common.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StarredRepoListScreen(
    navigateBack: () -> Unit,
    navigateToRepoDetails: (String) -> Unit,
    viewModel: StarredRepoListViewModel = viewModel(factory = AppViewModelProvider.factory),
) {
    val starredRepo by viewModel.starredRepo.collectAsStateWithLifecycle()
    val inProcess by viewModel.inProcess.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.Repositories),
                canNavigateBack = true,
                navigateUp = navigateBack
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn {
                items(starredRepo.repositories) { repository ->
                    val owner = repository.substringBefore("/").trim()
                    val name = repository.substringAfter("/").trim()
                    RepositoryItem(
                        owner = owner,
                        name = name,
                        onClick = { navigateToRepoDetails(repository) }
                    )
                }
            }
            if (inProcess) {
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
}