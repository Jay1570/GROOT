package com.example.groot.repositories

import android.util.Log
import com.example.groot.model.Friends
import com.example.groot.model.User
import com.example.groot.utility.FRIENDS_COLLECTION
import com.example.groot.utility.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class OtherUserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    val currentUserId get() = auth.currentUser?.uid ?: ""

    private val _profile = MutableStateFlow(User())
    val profile: StateFlow<User> get() = _profile

    private val _friends = MutableStateFlow(Friends())
    val friends: StateFlow<Friends> get() = _friends

    fun getProfile(userId: String) {
        val docRef = firestore.collection(USER_COLLECTION).document(userId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("OtherUserProfileViewModel", "Error fetching profile", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val userProfile = snapshot.toObject(User::class.java) ?: User()
                _profile.value = userProfile
            } else {
                _profile.value = User()
            }
        }
    }

    fun getFriends(userId: String) {
        val docRef = firestore.collection(FRIENDS_COLLECTION).document(userId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("OtherUserProfileViewModel", "Error fetching friends", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val userFriends = snapshot.toObject(Friends::class.java) ?: Friends()
                _friends.value = userFriends
            } else {
                _friends.value = Friends()
            }
        }
    }

    suspend fun follow(userId: String) {
        val docRef = firestore.collection(FRIENDS_COLLECTION).document(currentUserId)
        val docRefFollow = firestore.collection(FRIENDS_COLLECTION).document(userId)

        docRef.update("following", FieldValue.arrayUnion(userId)).await()
        docRefFollow.update("followers", FieldValue.arrayUnion(currentUserId)).await()
    }

    suspend fun unfollow(userId: String) {
        val docRef = firestore.collection(FRIENDS_COLLECTION).document(currentUserId)
        val docRefFollow = firestore.collection(FRIENDS_COLLECTION).document(userId)

        docRef.update("following", FieldValue.arrayRemove(userId)).await()
        docRefFollow.update("followers", FieldValue.arrayRemove(currentUserId)).await()
    }
}