package com.example.groot.screens.repository

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.FileContent
import com.example.groot.SnackbarEvent
import com.example.groot.SnackbarManager
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FileContentViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseStorage = FirebaseStorage.getInstance()

    val path = savedStateHandle.toRoute<FileContent>().path

    private val _uiState = MutableStateFlow(FileContentUiState())
    val uiState get() = _uiState.asStateFlow()

    fun fetchContent(path: String = this.path, navigateBack: () -> Unit) {
        _uiState.value = _uiState.value.copy(inProcess = true)
        val fileRef = firebaseStorage.reference.child(path)
        fileRef.metadata.addOnSuccessListener { meta ->
            if (meta.contentType!!.contains("image") || meta.name!!.endsWith(".webp")) {
                _uiState.value = _uiState.value.copy(inProcess = false)
                showSnackbar("File type not supported")
                navigateBack()
                return@addOnSuccessListener
            }
            fileRef.getBytes(meta.sizeBytes).addOnSuccessListener { bytes ->
                val content = String(bytes).lines().mapIndexed { index, s -> Pair(index + 1, s) }
                _uiState.update {
                    it.copy(content = content, inProcess = false)
                }
            }.addOnFailureListener { _ ->
                _uiState.value = _uiState.value.copy(inProcess = false)
                showSnackbar("Failed to open file")
                navigateBack()
            }
        }.addOnFailureListener { _ ->
            _uiState.value = _uiState.value.copy(inProcess = false)
            showSnackbar("Failed to open file")
            navigateBack()
        }
    }

    private fun showSnackbar(message: String) {
        viewModelScope.launch {
            SnackbarManager.sendEvent(SnackbarEvent(message))
        }
    }
}

data class FileContentUiState(
    val content: List<Pair<Int, String>> = emptyList(),
    val inProcess: Boolean = false
)