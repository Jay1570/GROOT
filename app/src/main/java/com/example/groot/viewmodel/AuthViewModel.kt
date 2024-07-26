package com.example.groot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groot.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection = "users"
    val hasUser: Boolean get() =  auth.currentUser != null

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    fun login(email: String, password: String): Boolean {
        var successful = false
        viewModelScope.launch {
            try {
                _user.value = auth.signInWithEmailAndPassword(email, password).await().user
                _authStatus.value = "Login Successful"
                successful = true
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup failed :- ${e.message.toString()}")
                _authStatus.value = e.message.toString()
                successful = false
            }
        }
        return successful
    }

    fun signup(email: String, password: String, imgUrl: String, userName: String): Boolean {
        var successful = false
        viewModelScope.launch {
            try {
                _user.value = auth.createUserWithEmailAndPassword(email, password).await().user
                val userId = user.value?.uid ?: ""
                val user = User(email = email, imgUrl = imgUrl, userName = userName, userId = userId)
                fireStore.collection(userCollection).document(userId).set(user).await()
                _authStatus.value = "Signup Successful"
                successful = true
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Signup failed :- ${e.message.toString()}")
                _authStatus.value = e.message.toString()
                successful = false
            }
        }
        return successful
    }

    fun signout() {
        auth.signOut()
        _user.value = auth.currentUser
        _authStatus.value = "SignedOut Successfully"
    }

    suspend fun checkUsername(userName: String): Boolean {
        return try {
            val querySnapshot = fireStore.collection("users")
                .whereEqualTo("userName", userName)
                .get()
                .await()
            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Error checking username", e)
            false
        }
    }
}