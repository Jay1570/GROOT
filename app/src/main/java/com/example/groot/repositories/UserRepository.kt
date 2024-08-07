package com.example.groot.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.groot.FRIENDS_COLLECTION
import com.example.groot.USER_COLLECTION
import com.example.groot.model.Friends
import com.example.groot.model.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val currentUserId get() = auth.currentUser?. uid ?: ""

    private val _profile = MutableLiveData<User>() // this is for the use who has logged in
    val profile: LiveData<User> get() = _profile

    private val _friends = MutableLiveData<Friends>()
    val friends: LiveData<Friends> get() = _friends

    private val _followerProfiles = MutableLiveData<List<User>>()
    val followerProfiles: LiveData<List<User>> get() = _followerProfiles

    private val _followingProfiles = MutableLiveData<List<User>>()
    val followingProfiles: LiveData<List<User>> = _followingProfiles

    private val _user = MutableLiveData<User>() // this for viewing other users
    val user: LiveData<User> get() = _user

    private val _userFriends = MutableLiveData<Friends>()
    val userFriends: LiveData<Friends> get() = _userFriends

    init {
        getProfile()
        getFriends()
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

    fun getProfile(userId: String) {
        val docRef = fireStore.collection(USER_COLLECTION).document(userId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("UserProfileViewModel", "Error fetching profile", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val userProfile = snapshot.toObject(User::class.java) ?: User()
                _user.value = userProfile
            } else {
                _user.value = User()
            }
        }
    }

    suspend fun fetchUsersByNameAndEmail(query: String) : List<User> {
        val usersByNameSnapshot = fireStore.collection(USER_COLLECTION)
            .whereGreaterThanOrEqualTo("userName", query)
            .whereLessThanOrEqualTo("userName", query + '\uf8ff')
            .get()
            .await()

        val usersByEmailSnapshot = fireStore.collection(USER_COLLECTION)
            .whereGreaterThanOrEqualTo("email", query)
            .whereLessThanOrEqualTo("email", query + '\uf8ff')
            .get()
            .await()

        val usersByName = usersByNameSnapshot.documents.mapNotNull { document ->
            document.toObject(User::class.java)
        }

        val usersByEmail = usersByEmailSnapshot.documents.mapNotNull { document ->
            document.toObject(User::class.java)
        }
        return (usersByName + usersByEmail).distinctBy { it.userId }
    }

    suspend fun follow(userId: String) {
        val docRef = fireStore.collection(FRIENDS_COLLECTION).document(currentUserId)
        val docRefFollow = fireStore.collection(FRIENDS_COLLECTION).document(userId)

        docRef.update("following", FieldValue.arrayUnion(userId)).await()
        docRefFollow.update("followers", FieldValue.arrayUnion(currentUserId)).await()
    }

    suspend fun unfollow(userId: String) {
        val docRef = fireStore.collection(FRIENDS_COLLECTION).document(currentUserId)
        val docRefFollow = fireStore.collection(FRIENDS_COLLECTION).document(userId)

        docRef.update("following", FieldValue.arrayRemove(userId)).await()
        docRefFollow.update("followers", FieldValue.arrayRemove(currentUserId)).await()
    }

    private fun getFriends() {
        if (currentUserId.isEmpty()) {
            _friends.value = Friends()
            return
        }
        val docRef = fireStore.collection(FRIENDS_COLLECTION).document(currentUserId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("GetFriends", "Error fetching friends", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val friends = snapshot.toObject(Friends::class.java) ?: Friends()
                _friends.value = friends
                CoroutineScope(Dispatchers.IO).launch {
                    getFollowerProfiles(friends.followers)
                    getFollowingProfiles(friends.following)
                }
            } else {
                _friends.value = Friends()
            }
        }
    }

    private suspend fun getFollowerProfiles(followerIds: List<String>) {
        if (followerIds.isEmpty()) {
            _followerProfiles.postValue(emptyList())
            return
        }

        val userFollowerRefs = followerIds.map { id ->
            fireStore.collection(USER_COLLECTION).document(id).get()
        }

        try {
            val followers = Tasks.whenAllSuccess<DocumentSnapshot>(userFollowerRefs).await()
            val profiles = followers.mapNotNull { it.toObject(User::class.java) }
            _followerProfiles.postValue(profiles)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error fetching follower profiles", e)
            _followerProfiles.postValue(emptyList())
        }
    }

    private suspend fun getFollowingProfiles(followingIds: List<String>) {
        if (followingIds.isEmpty()) {
            _followingProfiles.postValue(emptyList())
            return
        }

        val userFollowerRefs = followingIds.map { id ->
            fireStore.collection(USER_COLLECTION).document(id).get()
        }

        try {
            val following = Tasks.whenAllSuccess<DocumentSnapshot>(userFollowerRefs).await()
            val profiles = following.mapNotNull { it.toObject(User::class.java) }
            _followingProfiles.postValue(profiles)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error fetching follower profiles", e)
            _followingProfiles.postValue(emptyList())
        }
    }

    fun getUserFriends(userId: String) {
        val docRef = fireStore.collection(FRIENDS_COLLECTION).document(userId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("GetFriends", "Error fetching friends", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val friends = snapshot.toObject(Friends::class.java) ?: Friends()
                _userFriends.value = friends
            } else {
                _userFriends.value = Friends()
            }
        }
    }
}