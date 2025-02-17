package com.example.groot.screens.repository

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.AppViewModelProvider
import com.example.groot.R
import com.example.groot.common.TopBar
import com.example.groot.model.TreeNode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileListScreen(
    navigateToFileContent: (String) -> Unit,
    navigateBack: () -> Unit,
    viewModel: FileListViewModel = viewModel(factory = AppViewModelProvider.factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar(
                title = uiState.currNode!!.name,
                canNavigateBack = true,
                navigateUp = { if(!uiState.inProcess) viewModel.navigateBack(navigateBack) }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (uiState.inProcess) {
                Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            } else {
                LazyColumn {
                    items(uiState.fileList) {
                        FileItem(
                            node = it,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    if (it.isFolder) viewModel.navigateToFolder(it)
                                    else navigateToFileContent(it.path)
                                })
                                .padding(horizontal = 16.dp)
                                .heightIn(min = 56.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FileItem(
    node: TreeNode,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = if (node.isFolder) painterResource(id = R.drawable.folder_svgrepo_com) else painterResource(R.drawable.files_interface_svgrepo_com),
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .padding(top = 2.dp, start = 2.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = node.name,
            fontSize = 20.sp
        )
    }
}