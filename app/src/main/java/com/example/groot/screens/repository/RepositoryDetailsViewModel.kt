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
import com.example.groot.utility.LanguageData
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
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger

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
        val allFiles = Collections.synchronizedList(mutableListOf<StorageReference>())
        val pendingRequests = AtomicInteger(1)

        fun onComplete() {
            if (pendingRequests.decrementAndGet() == 0) {
                calculateLanguageContributions(allFiles)
            }
        }

        fun fetchFilesInDirectory(directory: StorageReference) {
            directory.listAll()
                .addOnSuccessListener { listResult ->
                    allFiles.addAll(listResult.items)
                    val subfolders = listResult.prefixes.filter { !it.name.startsWith(".") }
                    if (subfolders.isNotEmpty()) {
                        pendingRequests.addAndGet(subfolders.size)
                        subfolders.forEach { fetchFilesInDirectory(it) }
                    }
                    onComplete()
            }.addOnFailureListener { exception ->
                Log.e("RepoDetailsViewModel", "Error: ${exception.message}")
                onComplete()
            }
        }
        fetchFilesInDirectory(folderRef)
    }

    private fun calculateLanguageContributions(files: List<StorageReference>) {
        val languageCount = mutableMapOf<String, Int>()
        files.forEach { fileRef ->
            val extension = ".${fileRef.name.substringAfterLast(".", missingDelimiterValue = "").lowercase()}"
            val language = LanguageData.extensions[extension] ?: "Others"
            languageCount[language] = (languageCount[language] ?: 0) + 1
        }
        _uiState.update {
            it.copy(languageContributions = languageCount)
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