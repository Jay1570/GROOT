package com.example.groot.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.groot.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val userCollection = "users"
    private val currentUserId get() = auth.currentUser?. uid ?: ""
    val hasUser: Boolean get() = auth.currentUser != null

    private val _profile = MutableLiveData<User>()

    val profile: LiveData<User> get() = _profile

    init {
        if (currentUserId.isNotEmpty()) {
            val docRef = fireStore.collection(userCollection).document(currentUserId)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("UserProfileViewModel", "Error fetching profile", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val userProfile = snapshot.toObject(User::class.java) ?: User()
                    _profile.value = userProfile
                } else {
                    _profile.value = User() // Default user or handle as needed
                }
            }
        } else {
            // Handle case when user is not signed in or ID is invalid
            _profile.value = User()
        }
    }

    suspend fun signup(email: String, password: String, imgUrl: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        val userId = auth.currentUser?.uid ?: ""
        val user = User(email = email, imgUrl = imgUrl, userName = userName, userId = userId)
        fireStore.collection(userCollection).document(userId).set(user).await()
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        auth.signOut()
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