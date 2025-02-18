package com.example.groot.screens.repository

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
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
fun RepositoryListScreen(
    navigateBack: () -> Unit,
    navigateToRepoDetails: (String) -> Unit,
    viewModel: RepositoryListViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val repoList by viewModel.repoList.collectAsStateWithLifecycle()
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
            if (repoList.isEmpty() && !inProcess) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.no_repositories),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            } else {
                LazyColumn {
                    items(repoList) { repository ->
                        RepositoryItem(
                            owner = repository.owner,
                            name = repository.name,
                            onClick = { navigateToRepoDetails("${repository.owner} / ${repository.name}") }
                        )
                    }
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