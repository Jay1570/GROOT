package com.example.groot.repositories

import android.util.Log
import com.example.groot.model.Friends
import com.example.groot.model.Repository
import com.example.groot.model.StarredRepositories
import com.example.groot.model.User
import com.example.groot.utility.FRIENDS_COLLECTION
import com.example.groot.utility.REPOSITORY_COLLECTION
import com.example.groot.utility.STARRED_REPOSITORY
import com.example.groot.utility.USER_COLLECTION
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    private val currentUserId get() = auth.currentUser?.uid ?: ""

    private val _profile = MutableStateFlow(User()) // this is for the use who has logged in
    val profile: StateFlow<User> get() = _profile

    private val _friends = MutableStateFlow(Friends())
    val friends: StateFlow<Friends> get() = _friends

    private val _followerProfiles = MutableStateFlow<List<User>>(emptyList())
    val followerProfiles: StateFlow<List<User>> get() = _followerProfiles

    private val _followingProfiles = MutableStateFlow<List<User>>(emptyList())
    val followingProfiles: StateFlow<List<User>> = _followingProfiles

    private val _starredRepositories = MutableStateFlow(StarredRepositories())
    val starredRepositories: StateFlow<StarredRepositories> get() = _starredRepositories

    fun getProfile() {
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
        return (usersByName + usersByEmail).distinctBy { it.userId }.filter { it.id != currentUserId }
    }

    suspend fun searchRepository(query: String): List<Repository> {
        val repositories = fireStore.collection(REPOSITORY_COLLECTION)
            .whereEqualTo("isPrivate", false)
            .orderBy("name")
            .startAt(query.uppercase())
            .endAt(query.lowercase() + '\uf8ff')
            .get()
            .await()

        val repository = repositories.documents.mapNotNull { document ->
            document.toObject(Repository::class.java)
        }

        return repository
    }

    fun getFriends() {
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
            _followerProfiles.value = emptyList()
            return
        }

        val userFollowerRefs = followerIds.map { id ->
            fireStore.collection(USER_COLLECTION).document(id).get()
        }

        try {
            val followers = Tasks.whenAllSuccess<DocumentSnapshot>(userFollowerRefs).await()
            val profiles = followers.mapNotNull { it.toObject(User::class.java) }
            _followerProfiles.value = profiles
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error fetching follower profiles", e)
            _followerProfiles.value = emptyList()
        }
    }

    private suspend fun getFollowingProfiles(followingIds: List<String>) {
        if (followingIds.isEmpty()) {
            _followingProfiles.value = emptyList()
            return
        }

        val userFollowerRefs = followingIds.map { id ->
            fireStore.collection(USER_COLLECTION).document(id).get()
        }

        try {
            val following = Tasks.whenAllSuccess<DocumentSnapshot>(userFollowerRefs).await()
            val profiles = following.mapNotNull { it.toObject(User::class.java) }
            _followingProfiles.value = profiles
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error fetching follower profiles", e)
            _followingProfiles.value = emptyList()
        }
    }

    fun getStarredRepositories() {
        val docRef = fireStore.collection(STARRED_REPOSITORY).document(currentUserId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("UserRepository", e.message.toString())
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                _starredRepositories.value = snapshot.toObject(StarredRepositories::class.java) ?: StarredRepositories()
            }
        }
    }
}