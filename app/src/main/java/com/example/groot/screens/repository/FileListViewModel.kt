package com.example.groot.screens.repository

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.groot.FileList
import com.example.groot.model.TreeNode
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Stack

class FileListViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    private val firebaseStorage = FirebaseStorage.getInstance()

    private val parents = Stack<TreeNode>()
    private val path = savedStateHandle.toRoute<FileList>().path

    private val _uiState = MutableStateFlow(FileListUiState())
    val uiState get() = _uiState.asStateFlow()

    init {
        initializeRoot(path)
    }

    private fun initializeRoot(path: String = this.path) {
        if (_uiState.value.currNode != null || parents.isNotEmpty()) return

        _uiState.update {
            it.copy(currNode = TreeNode(name = path.substringAfterLast("/").trim(), isFolder = true, isExpanded = false, path = path), inProcess = true)
        }

        viewModelScope.launch {
            fetchList(_uiState.value.currNode!!)
        }
    }


    private fun fetchList(node: TreeNode) {
        _uiState.update { it.copy(inProcess = true) }
        val reference = firebaseStorage.reference.child(node.path)
        reference.listAll().addOnSuccessListener { listResult ->
            for (folderRef in listResult.prefixes) {
                if (folderRef.name == ".groot" || folderRef.name == ".git") continue
                val folderNode = TreeNode(name = folderRef.name, isFolder = true, path = folderRef.path)
                node.children.add(folderNode)
            }
            for (fileRef in listResult.items) {
                if (fileRef.name == "user.txt") continue
                val fileNode = TreeNode(name = fileRef.name, isFolder = false, path = fileRef.path)
                node.children.add(fileNode)
            }
            _uiState.update {
                it.copy(inProcess = false)
            }
        }.addOnFailureListener { e ->
            Log.e("FileListViewModel", e.message.toString())
            _uiState.value = _uiState.value.copy(inProcess = false)
        }
    }

    fun navigateToFolder(node: TreeNode) {
        _uiState.value = _uiState.value.copy(inProcess = true)
        parents.push(_uiState.value.currNode)
        _uiState.update {
            it.copy(currNode = node, isParentsEmpty = false, inProcess = false)
        }
        if (_uiState.value.currNode!!.children.isEmpty()) {
            viewModelScope.launch {
                fetchList(_uiState.value.currNode!!)
            }
        }
    }

    fun navigateBack(navigateBack: () -> Unit) {
        _uiState.update { it.copy(inProcess = true) }
        if (_uiState.value.isParentsEmpty) {
            _uiState.update { it.copy(inProcess = false) }
            navigateBack()
        } else {
            val parentNode = parents.pop()
            _uiState.update {
                it.copy(
                    currNode = parentNode,
                    isParentsEmpty = parents.isEmpty(),
                    inProcess = false
                )
            }
        }
    }
}

data class FileListUiState(
    val inProcess: Boolean = false,
    val isParentsEmpty: Boolean = true,
    val currNode: TreeNode? = null
)