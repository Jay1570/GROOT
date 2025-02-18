package com.example.groot.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.R
import com.example.groot.common.RepositoryItem
import com.example.groot.common.TopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    navigateToRepository: (String) -> Unit,
    navigateToUserSearch: (String) -> Unit,
    navigateToRepoSearch: (String) -> Unit,
    viewModel: ExploreViewModel = viewModel()
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    val suggestions = remember { mutableStateListOf<String>() }
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        viewModel.fetchRepo()
    }

    Scaffold(
        topBar = {
            TopBar(
                title = stringResource(R.string.explore),
                canNavigateBack = false,
                actions = {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { newText ->
                                searchQuery = newText
                                suggestions.clear()
                                if (newText.length >= 2) {
                                    suggestions.add("Search Users with \"$newText\"")
                                    suggestions.add("Search Repositories with \"$newText\"")
                                }
                            },
                            placeholder = { Text("Search...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                            trailingIcon = {
                                IconButton(onClick = {
                                    searchQuery = ""
                                    isSearching = false
                                    suggestions.clear()
                                }) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    } else {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                        }
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->

        val repositories by viewModel.exploreRepository.collectAsStateWithLifecycle()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                if (isSearching && suggestions.isNotEmpty()) {
                    LazyColumn(
                        Modifier.background(MaterialTheme.colorScheme.background).fillMaxSize().padding(horizontal = 16.dp)
                    ) {
                        items(suggestions) { suggestion ->
                            TextButton(
                                onClick = {
                                    if (suggestion.contains("Users")) {
                                        navigateToUserSearch(searchQuery)
                                    } else {
                                        navigateToRepoSearch(searchQuery)
                                    }
                                    searchQuery = ""
                                    isSearching = false
                                    suggestions.clear()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.background,
                                    contentColor = MaterialTheme.colorScheme.onBackground,
                                ),
                            ) {
                                Text(suggestion, modifier = Modifier.padding(8.dp).weight(1f))
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Repositories You may Like",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp).background(MaterialTheme.colorScheme.surfaceContainerLow)
                    )

                    LazyColumn {
                        items(repositories) { repository ->
                            RepositoryItem(
                                owner = repository.owner,
                                name = repository.name,
                                onClick = { navigateToRepository("${repository.owner} / ${repository.name}") }
                            )
                        }
                    }
                }
            }
        }
    }
}