package com.example.groot.screens.repository

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.RepositoryList
import com.example.groot.model.Repository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RepositoryListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseStorage = FirebaseStorage.getInstance()

    private val _repoList = MutableStateFlow<List<Repository>>(emptyList())
    val repoList get() = _repoList.asStateFlow()

    private var username: String = ""

    private val _inProcess = MutableStateFlow(false)
    val inProcess get() = _inProcess.asStateFlow()

    init {
        username = savedStateHandle.toRoute<RepositoryList>().username
        fetchRepositories(username)
    }

    private fun fetchRepositories(username: String = this.username) {
        viewModelScope.launch {
            _inProcess.value = true
            firebaseStorage.reference.child("$username ").listAll().addOnSuccessListener { result ->
                val list = mutableListOf<Repository>()
                result.prefixes.forEach {
                    list.add(Repository(name = it.name.trim(), owner = username))
                }
                _repoList.value = list
                _inProcess.value = false
            }.addOnFailureListener {
                _inProcess.value = false
                Log.e("RepoListViewModel","Error :- ${it.message.toString()}")
            }
        }
    }
}