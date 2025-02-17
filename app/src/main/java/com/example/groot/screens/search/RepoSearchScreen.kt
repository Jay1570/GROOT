package com.example.groot.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun RepoSearchScreen(
    navigateBack: () -> Unit,
    navigateToRepository: (String) -> Unit,
    viewModel: RepoSearchViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val repoList by viewModel.repoList.collectAsStateWithLifecycle()
    val inProcess by viewModel.inProcess.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.Search),
                canNavigateBack = true,
                navigateUp = navigateBack,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (repoList.isEmpty() && !inProcess) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.no_repositories),
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = innerPadding
                ) {
                    items(repoList) { repository ->
                        RepositoryItem(
                            owner = repository.owner,
                            name = repository.name,
                            onClick = { navigateToRepository("${repository.owner} / ${repository.name}") }
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