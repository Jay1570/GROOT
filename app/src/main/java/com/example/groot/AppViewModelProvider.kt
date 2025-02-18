package com.example.groot

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.groot.screens.repository.*
import com.example.groot.screens.search.RepoSearchViewModel
import com.example.groot.screens.search.UserSearchViewModel
import com.example.groot.screens.settings.SettingsViewModel
import com.example.groot.screens.user.UserViewModel

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

        initializer {
            RepositoryDetailsViewModel(this.createSavedStateHandle())
        }

        initializer {
            FileListViewModel(this.createSavedStateHandle())
        }

        initializer {
            FileContentViewModel(this.createSavedStateHandle())
        }

        initializer {
            UserViewModel(this.createSavedStateHandle())
        }

        initializer {
            SettingsViewModel(myApp().themePreference)
        }
    }
}

fun CreationExtras.myApp(): MyApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApp)