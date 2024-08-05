package com.example.groot.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.groot.FOLLOWERS_COLLECTION
import com.example.groot.FOLLOWING_COLLECTION
import com.example.groot.USER_COLLECTION
import com.example.groot.model.Followers
import com.example.groot.model.Following
import com.example.groot.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val currentUserId get() = auth.currentUser?. uid ?: ""
    val hasUser: Boolean get() = auth.currentUser != null

    private val _profile = MutableLiveData<User>()
    val profile: LiveData<User> get() = _profile

    private val _followers = MutableLiveData<Followers>()
    val followers: LiveData<Followers> get() = _followers

    private val _following = MutableLiveData<Following>()
    val following: LiveData<Following> get() = _following

    private val _followerProfile = MutableLiveData<List<User>>()
    val followerProfile: LiveData<List<User>> get() = _followerProfile

    init {
        getProfile()
        getFollowers()
        getFollowing()
    }

    private fun getProfile() {
        if (currentUserId.isNotEmpty()) {
            val docRef = fireStore.collection(USER_COLLECTION).document(currentUserId)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("UserProfileViewModel", "Error fetching profile", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val userProfile = snapshot.toObject(User::class.java) ?: User()
                    _profile.value = userProfile
                } else {
                    _profile.value = User()
                }
            }
        } else {
            _profile.value = User()
        }
    }

    private fun getFollowers() {
        if (currentUserId.isNotEmpty()) {
            val docRef = fireStore.collection(FOLLOWERS_COLLECTION).document(currentUserId)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("UserProfileViewModel", "Error fetching followers", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val followers = snapshot.toObject(Followers::class.java) ?: Followers()
                    _followers.value = followers
                    CoroutineScope(Dispatchers.IO).launch {
                        getFollowerProfiles(followers)
                    }
                } else {
                    _followers.value = Followers()
                }
            }
        } else {
            _followers.value = Followers()
        }
    }

    private fun getFollowing() {
        if (currentUserId.isNotEmpty()) {
            val docRef = fireStore.collection(FOLLOWING_COLLECTION).document(currentUserId)
            docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("UserProfileViewModel", "Error fetching following", e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val following = snapshot.toObject(Following::class.java) ?: Following()
                    _following.value = following
                } else {
                    _following.value = Following()
                }
            }
        } else {
            _following.value = Following()
        }
    }

    private suspend fun getFollowerProfiles(followers: Followers) {
        val userIds = followers.followers

        if (userIds.isEmpty()) {
            _followerProfile.value = emptyList()
            return
        }

        val userRefs = userIds.map { id ->
            fireStore.collection(USER_COLLECTION).document(id).get()
        }

        try {
            val documents = Tasks.whenAllSuccess<DocumentSnapshot>(userRefs).await()
            val profiles = documents.mapNotNull { it.toObject(User::class.java) }
            _followerProfile.postValue(profiles)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error fetching follower profiles", e)
            _followerProfile.postValue(emptyList())
        }
    }

    suspend fun signup(email: String, password: String, imgUrl: String, userName: String) {
        auth.createUserWithEmailAndPassword(email, password).await()
        val userId = auth.currentUser?.uid ?: ""
        val user = User(email = email, imgUrl = imgUrl, userName = userName, userId = userId)
        val followers = Followers(userId = userId)
        val following = Following(userId = userId)
        fireStore.collection(USER_COLLECTION).document(userId).set(user).await()
        fireStore.collection(FOLLOWERS_COLLECTION).document(userId).set(followers).await()
        fireStore.collection(FOLLOWING_COLLECTION).document(userId).set(following).await()
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