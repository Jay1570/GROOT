package com.example.groot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.groot.model.Repository
import com.example.groot.repositories.RepositoryData
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RepoDetailsViewModel : ViewModel() {

    private val firebaseStorage = FirebaseStorage.getInstance()

    private val repository = RepositoryData()

    private val _readmeContent = MutableLiveData<String>()
    val readmeContent: LiveData<String> get() = _readmeContent

    private val _languageContributions = MutableLiveData<Map<String, Float>>()
    val languageContributions: LiveData<Map<String, Float>> get() = _languageContributions

    private val _isStarred = MutableLiveData<Boolean>()
    val isStarred: LiveData<Boolean> get() = _isStarred

    private val _starCount = MutableLiveData<Int>()
    val starCount: LiveData<Int> get() = _starCount

    val repositoryDetails: LiveData<Repository> = repository.repository.asLiveData()

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val repoObserver = Observer<Repository> { repo ->
        _isStarred.value = repo.stars.contains(repository.currentUserId)
        _starCount.value = repo.stars.size
    }

    init {
        repositoryDetails.observeForever(repoObserver)
    }

    fun starRepo(path: String) {
        viewModelScope.launch {
            repository.starRepo(path)
        }
    }

    fun unstarRepo(path: String) {
        viewModelScope.launch {
            repository.unstarRepo(path)
        }
    }

    fun fetchReadmeAndContributions(path: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                repository.getRepository(path)
                fetchReadmeFile(path)
                fetchFilesAndCalculateContributions(path)
            } catch (e : Exception) {
                _error.value = e.message.toString()
            }
        }.invokeOnCompletion {
            _isLoading.value = false
        }
    }

    private suspend fun fetchReadmeFile(path: String) {
        val fileRef = firebaseStorage.reference.child("$path/README.md")

        val ONE_MEGABYTE: Long = 1024 * 1024
        try {
            _readmeContent.value = String(fileRef.getBytes(ONE_MEGABYTE).await())
        } catch (e: StorageException) {
            if (e.errorCode == StorageException.ERROR_OBJECT_NOT_FOUND) {
                _readmeContent.value = "No Description Provided"
            } else {
                _error.value = e.message.toString()
            }
        } catch (e: Exception) {
            _error.value = e.message.toString()
        }
    }

    private fun fetchFilesAndCalculateContributions(path: String) {
        val folderRef = firebaseStorage.reference.child(path)
        fetchFilesInDirectory(folderRef) { files ->
            if (files.isNotEmpty()) {
                calculateLanguageContributions(files) {
                    _languageContributions.value = it
                }
            }
        }
        Log.d("repo_details", "Total Languages :- ${languageContributions.value?.size}")
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
                _error.value = exception.message.toString()
            }
        }
    }

    private fun calculateLanguageContributions(files: List<StorageReference>, callback: (Map<String, Float>) -> Unit) {
        val languageCount = mutableMapOf<String, Int>()
        var pendingFiles = files.size
        files.forEach { fileRef ->
            fileRef.metadata.addOnSuccessListener { metadata ->
                if (metadata.contentType!!.startsWith("text/")) {
                    val language = fileRef.name.substringAfterLast(".")
                    languageCount[language] = (languageCount[language] ?: 0) + 1
                }
                if (--pendingFiles == 0) {
                    val totalFiles = files.size
                    val contributions = languageCount.mapValues { (it.value * 100f) / totalFiles }
                    callback(contributions)
                }
            }.addOnFailureListener { error ->
                Log.e("repo_details", error.message.toString())
                if (--pendingFiles == 0) {
                    val totalFiles = files.size
                    val contributions = languageCount.mapValues { (it.value * 100f) / totalFiles }
                    callback(contributions)
                }
            }
        }
    }
    override fun onCleared() {
        super.onCleared()
        repositoryDetails.removeObserver(repoObserver)
    }
}