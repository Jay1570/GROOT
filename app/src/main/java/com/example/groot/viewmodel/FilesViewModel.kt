package com.example.groot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.model.TreeNode
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.launch
import java.util.Stack

class FilesViewModel : ViewModel() {

    private val TAG = "FilesViewModel"
    private val firebaseStorage = FirebaseStorage.getInstance()
    private lateinit var rootRef: StorageReference

    private val _fileList = MutableLiveData<List<TreeNode>>()
    val fileList: LiveData<List<TreeNode>> get() = _fileList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _fileContent = MutableLiveData<String?>()
    val fileContent: LiveData<String?> get() = _fileContent

    var fileName: String = ""

    private val parents = Stack<TreeNode>()
    var isParentsEmpty: Boolean = true
    private var currNode: TreeNode? = null

    fun initializeRoot(path: String) {
        if(currNode != null || parents.isNotEmpty()) return
        _isLoading.value = true
        rootRef = firebaseStorage.reference.child(path)
        currNode = TreeNode(name = path, isFolder = true, isExpanded = false, path = path)
        viewModelScope.launch {
            fetchList(rootRef, currNode!!)
        }
    }

    private fun fetchList(reference: StorageReference, node: TreeNode) {
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
            _fileList.value = node.children
            _isLoading.value = false
        }.addOnFailureListener { e ->
            Log.e(TAG, e.message.toString())
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun openFile(path: String, fileName: String) {
        _isLoading.value = true
        this.fileName = fileName
        val fileRef = FirebaseStorage.getInstance().reference.child(path)
        fileRef.metadata.addOnSuccessListener { meta ->
            if (meta.contentType!!.contains("image") || meta.name!!.endsWith(".webp")) {
                _isLoading.value = false
                _error.value = "File type not supported"
                return@addOnSuccessListener
            }
            fileRef.getBytes(meta.sizeBytes).addOnSuccessListener { bytes ->
                _fileContent.value = String(bytes)
                _isLoading.value = false
            }.addOnFailureListener { e ->
                _isLoading.value = false
                _error.value = "Failed to open file"
                Log.e(TAG, "Failed to Open File", e)
            }
        }.addOnFailureListener { e ->
            _isLoading.value = false
            Log.e(TAG, "Failed to Open File", e)
            _error.value = "Failed to open file"
        }
    }

    fun navigateToFolder(node: TreeNode) {
        _isLoading.value = true
        parents.push(currNode)
        currNode = node
        if (currNode!!.children.isEmpty()) {
            viewModelScope.launch {
                fetchList(firebaseStorage.reference.child(currNode!!.path), currNode!!)
            }
        }
        isParentsEmpty = false
    }

    fun navigateBack() {
        _isLoading.value = true
        if (parents.isNotEmpty()) {
            currNode = parents.pop()
            _fileList.value = currNode?.children
        }
        isParentsEmpty = parents.isEmpty()
        _isLoading.value = false
    }
}