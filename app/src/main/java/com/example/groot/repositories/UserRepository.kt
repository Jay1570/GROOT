package com.example.groot.repositories

import android.util.Log
import com.example.groot.model.Friends
import com.example.groot.model.Repository
import com.example.groot.model.User
import com.example.groot.utility.FRIENDS_COLLECTION
import com.example.groot.utility.REPOSITORY_COLLECTION
import com.example.groot.utility.USER_COLLECTION
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    val currentUserId get() = auth.currentUser?.uid ?: ""

    fun getUserId(): String {
        return currentUserId
    }

    suspend fun getUsername(): String {
        return fireStore.collection(USER_COLLECTION).document(currentUserId).get().await().toObject(User::class.java)?.userName ?: ""
    }

    fun getProfile(userId: String = currentUserId): Flow<User> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(User())
            close()
            return@callbackFlow
        }
        val listener = fireStore.collection(USER_COLLECTION).document(userId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("UserRepository", e.message.toString())
                close(e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java) ?: User()
                trySend(user)
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun getFriends(userId: String = currentUserId): Flow<Friends> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(Friends())
            close()
            return@callbackFlow
        }
        val listener = fireStore.collection(FRIENDS_COLLECTION).document(userId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("UserRepository", e.message.toString())
                close(e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val friends = snapshot.toObject(Friends::class.java) ?: Friends()
                trySend(friends)
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

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
            .whereEqualTo("private", false)
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

    fun getFriendsProfiles(userIds: List<String>): Flow<List<User>> = callbackFlow {
        if (userIds.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val task = userIds.map { id ->
            fireStore.collection(USER_COLLECTION).document(id).get()
        }

        try {
            val followers = Tasks.whenAllSuccess<DocumentSnapshot>(task).await()
            val profiles = followers.mapNotNull { it.toObject(User::class.java) }
            trySend(profiles)
        } catch (e: Exception) {
            trySend(emptyList())
            Log.e("AuthRepository", "Error fetching follower profiles", e)
        }
        close()
    }.flowOn(Dispatchers.IO)
}