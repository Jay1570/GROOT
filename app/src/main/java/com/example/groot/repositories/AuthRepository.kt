package com.example.groot.repositories

import android.util.Log
import com.example.groot.model.Friends
import com.example.groot.model.User
import com.example.groot.utility.FRIENDS_COLLECTION
import com.example.groot.utility.USER_COLLECTION
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    val hasUser: Boolean get() = auth.currentUser != null

    suspend fun signup(email: String, password: String, imgUrl: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        val userId = auth.currentUser?.uid ?: ""
        val user = User(email = email, imgUrl = imgUrl, userName = userName, userId = userId)
        val friends = Friends(userId = userId)
        fireStore.collection(USER_COLLECTION).document(userId).set(user).await()
        fireStore.collection(FRIENDS_COLLECTION).document(userId).set(friends).await()
    }

    suspend fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun updatePassword(oldPass: String, newPass: String): Boolean {
        val user = auth.currentUser ?: return false
        val email = user.email ?: return false
        val credential = EmailAuthProvider.getCredential(email, oldPass)
        user.reauthenticate(credential).await()
        user.updatePassword(newPass).await()
        return true
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