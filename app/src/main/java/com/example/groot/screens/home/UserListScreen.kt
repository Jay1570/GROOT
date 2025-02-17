package com.example.groot.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.groot.common.UserItem
import com.example.groot.model.User

@Composable
fun UserListScreen(
    list: List<User>,
    navigateToUser: (String) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        if (list.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "No Users",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyColumn {
                items(list) {
                    UserItem(
                        user = it,
                        onClick = { navigateToUser(it.userId) }
                    )
                }
            }
        }
    }
}