package com.example.groot

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.groot.screens.repository.RepositoryListViewModel
import com.example.groot.screens.repository.StarredRepoListViewModel
import com.example.groot.screens.search.RepoSearchViewModel
import com.example.groot.screens.search.UserSearchViewModel

object AppViewModelProvider {
    val factory = viewModelFactory {
        initializer {
            RepoSearchViewModel(this.createSavedStateHandle())
        }

        initializer {
            UserSearchViewModel(this.createSavedStateHandle())
        }

        initializer {
            RepositoryListViewModel(this.createSavedStateHandle())
        }

        initializer {
            StarredRepoListViewModel(this.createSavedStateHandle())
        }
    }
}