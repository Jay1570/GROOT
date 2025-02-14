package com.example.groot.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.groot.R
import com.example.groot.TopBar
import com.example.groot.ui.theme.GROOTTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    navigateToRepoList: () -> Unit,
    navigateToStarredList: () -> Unit,
    navigateToRepoSearch: (String) -> Unit,
    navigateToUserSearch: (String) -> Unit
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isSearching by rememberSaveable { mutableStateOf(false) }
    val suggestions = remember { mutableStateListOf<String>() }
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            TopBar(
                title = if (isSearching) "" else "Home",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                if (isSearching && suggestions.isNotEmpty()) {
                    LazyColumn {
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
                        text = stringResource(id = R.string.my_work),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    TextButton(
                        onClick = navigateToRepoList,
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
                        onClick = navigateToStarredList,
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
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    GROOTTheme {
        Surface {
            HomeContent(
                navigateToRepoList = {},
                navigateToStarredList = {},
                navigateToRepoSearch = {},
                navigateToUserSearch = {}
            )
        }
    }
}