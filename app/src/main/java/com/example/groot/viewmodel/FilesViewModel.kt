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

    private val _rootList = MutableLiveData<List<TreeNode>>()
    val rootList: LiveData<List<TreeNode>> get() = _rootList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val _fileContent = MutableLiveData<String>()
    val fileContent: LiveData<String> get() = _fileContent

    var fileName: String = ""

    private val parents = Stack<TreeNode>()
    var isParentsEmpty: Boolean = true
    private var currNode: TreeNode? = null
    private var activeCalls = 0

    fun initializeRoot(path: String) {
        _isLoading.value = true
        rootRef = firebaseStorage.reference.child(path)
        currNode = TreeNode(name = path, isFolder = true, isExpanded = false, path = path)
        viewModelScope.launch {
            fetchFirebaseData(rootRef, currNode!!, 0)
        }
    }

    private fun fetchFirebaseData(reference: StorageReference, node: TreeNode, callStack: Int) {
        activeCalls++
        if(callStack == 0) _isLoading.value = true
        Log.d(TAG, activeCalls.toString())
        reference.listAll().addOnSuccessListener { listResult ->
            for (folderRef in listResult.prefixes) {
                if (folderRef.name == ".groot" || folderRef.name == ".git") continue
                val folderNode = TreeNode(name = folderRef.name, isFolder = true, path = folderRef.path)
                node.children.add(folderNode)
                fetchFirebaseData(folderRef, folderNode, callStack+1)
            }
            for (fileRef in listResult.items) {
                if (fileRef.name == "user.txt") continue
                val fileNode = TreeNode(name = fileRef.name, isFolder = false, path = fileRef.path)
                node.children.add(fileNode)
            }
            if (callStack == 0) {
                _fileList.value = node.children
                _rootList.value = node.children
            }
            activeCalls--
            if (activeCalls == 0) _isLoading.value = false
        }.addOnFailureListener { e ->
            Log.e(TAG, e.message.toString())
            activeCalls--
            if (activeCalls == 0) _isLoading.value = false
        }
        Log.d(TAG, activeCalls.toString())
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
        _fileList.value = currNode?.children
        isParentsEmpty = false
        _isLoading.value = false
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