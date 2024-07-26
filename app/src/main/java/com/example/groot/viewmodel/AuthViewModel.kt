package com.example.groot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.groot.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val fireStore: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _authStatus = MutableLiveData<String>()
    val authStatus: LiveData<String> get() = _authStatus

    fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful){
                _user.value = auth.currentUser
                _authStatus.value = "Login Successful"
            } else {
                _authStatus.value = task.exception?.message ?: "Login Failed"
                Log.e("Login", task.exception?.message ?: "Unknown error")
            }
        }
    }

    fun signup(email: String, password: String, imgUrl: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _user.value = auth.currentUser
                val userId = auth.currentUser?.uid ?: ""
                Log.d("AuthViewModel", "Signup successful, user ID: $userId")
                Log.d("AuthViewModel", "User email: ${auth.currentUser?.email}")
                val user = User(userId = userId, email = email, imgUrl = imgUrl, userName = userName)
                _authStatus.value = "Signup Successful"
                fireStore.collection("users").document(userId).set(user).addOnCompleteListener{ task1 ->
                    if (task1.isSuccessful) {
                        _authStatus.value = "Profile Created"
                    } else {
                        _authStatus.value = "Profile Creation failed"
                        Log.e("AuthViewModel", "Profile creation failed: ${task1.exception?.message}")
                    }
                }
            } else {
                _authStatus.value = task.exception?.message ?: "Signup Failed"
                Log.e("AuthViewModel", "Signup failed: ${task.exception?.message}")
            }
        }
    }

    fun signout() {
        auth.signOut()
        _user.value = auth.currentUser
        _authStatus.value = "SignedOut Successfully"
    }
}