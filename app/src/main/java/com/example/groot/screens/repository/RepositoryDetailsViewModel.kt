package com.example.groot.screens.repository

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.RepositoryDetails
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.example.groot.model.Repository
import com.example.groot.repositories.RepositoryData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RepositoryDetailsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseStorage = FirebaseStorage.getInstance()
    private val repository = RepositoryData()

    val path = savedStateHandle.toRoute<RepositoryDetails>().path

    private val _uiState = MutableStateFlow(RepoDetailsUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        fetchRepository(path)
        fetchReadmeAndContributions(path)
    }

    private fun fetchRepository(path: String = this.path) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(inProcess = true)
            repository.getRepositoryFlow(path)
                .catch { e -> Log.e("RepositoryDetailsViewModel", e.message.toString()) }
                .collectLatest { repo ->
                    _uiState.update {
                        it.copy(repository = repo, inProcess = false, isStarred = repo.stars.contains(repository.currentUserId), starCount = repo.stars.size)
                    }
                }
        }
    }

    fun starOrUnstarRepo(path: String = this.path) {
        viewModelScope.launch {
            if (_uiState.value.isStarred) {
                repository.unstarRepo(path)
            } else {
                repository.starRepo(path)
            }
        }
    }

    private fun fetchReadmeAndContributions(path: String = this.path) {
        viewModelScope.launch {
            fetchReadmeFile(path)
            fetchFilesAndCalculateContributions(path)
        }
    }

    private suspend fun fetchReadmeFile(path: String) {
        val fileRef = firebaseStorage.reference.child("$path/README.md")

        val mb: Long = 1024 * 1024
        try {
            val readmeContent = fileRef.getBytes(mb).await().toString(Charsets.UTF_8)
            _uiState.update { it.copy(readmeContent = readmeContent) }
        } catch (e: StorageException) {
            if (e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                _uiState.update {  it.copy(readmeContent = "No Description Provided") }
            } else {
                SnackbarManager.sendEvent(SnackbarEvent("Error fetching README: ${e.message}"))
            }
        } catch (e: Exception) {
            SnackbarManager.sendEvent(SnackbarEvent("Error fetching README: ${e.message}"))
        }
    }

    private fun fetchFilesAndCalculateContributions(path: String) {
        val folderRef = firebaseStorage.reference.child(path)
        fetchFilesInDirectory(folderRef) { files ->
            if (files.isNotEmpty()) {
                calculateLanguageContributions(files) { contribution ->
                    _uiState.update { it.copy(languageContributions = contribution) }
                }
            }
        }
    }

    private fun fetchFilesInDirectory(directory: StorageReference, callback: (List<StorageReference>) -> Unit) {
        directory.listAll().addOnSuccessListener {
            directory.listAll().addOnSuccessListener { listResult ->
                val allFiles = ArrayList(listResult.items)
                val prefixes = listResult.prefixes

                if (prefixes.isEmpty()) {
                    callback(allFiles)
                } else {
                    var pendingPrefixes = prefixes.size
                    prefixes.forEach { folderRef ->
                        if (!folderRef.name.startsWith(".")) {
                            fetchFilesInDirectory(folderRef) { files ->
                                allFiles.addAll(files)
                                if (--pendingPrefixes == 0) {
                                    callback(allFiles)
                                }
                            }
                        } else {
                            callback(allFiles)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                Log.e("RepoDetailsViewModel",exception.message.toString())
            }
        }
    }

    private fun calculateLanguageContributions(files: List<StorageReference>, callback: (Map<String, Int>) -> Unit) {
        val languageCount = mutableMapOf<String, Int>()
        var pendingFiles = files.size
        files.forEach { fileRef ->
            fileRef.metadata.addOnSuccessListener { metadata ->
                if (metadata.contentType!!.startsWith("text/")) {
                    val language = fileRef.name.substringAfterLast(".")
                    languageCount[language] = (languageCount[language] ?: 0) + 1
                }
                if (--pendingFiles == 0) {
                    callback(languageCount)
                }
            }.addOnFailureListener { error ->
                Log.e("repo_details", error.message.toString())
                if (--pendingFiles == 0) {
                    callback(languageCount)
                }
            }
        }
    }
}

data class RepoDetailsUiState(
    val repository: Repository = Repository(),
    val isStarred: Boolean = false,
    val starCount: Int = 0,
    val languageContributions: Map<String, Int> = emptyMap(),
    val readmeContent: String = "",
    val inProcess: Boolean = false,
)

