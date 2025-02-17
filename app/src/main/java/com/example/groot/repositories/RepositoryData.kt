package com.example.groot.repositories

import android.util.Log
import com.example.groot.model.Friends
import com.example.groot.model.Repository
import com.example.groot.model.StarredRepositories
import com.example.groot.utility.REPOSITORY_COLLECTION
import com.example.groot.utility.STARRED_REPOSITORY
import com.example.groot.utility.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await

class RepositoryData {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()

    val currentUserId get() = auth.currentUser?.uid ?: ""

    private var repoId = ""

    private val _exploreRepositories = MutableStateFlow<List<Repository>>(emptyList())
    val exploreRepositories: StateFlow<List<Repository>> get() = _exploreRepositories

    fun getRepositoryFlow(path: String): Flow<Repository> = callbackFlow {
        val owner = path.substringBefore("/").trim()
        val repoName = path.substringAfter("/").trim()
        val query = fireStore.collection(REPOSITORY_COLLECTION).whereEqualTo("name", repoName).whereEqualTo("owner", owner).get().await()
        if (query.documents.isEmpty()) {
            val repo = Repository(name = repoName, owner = owner, private = false, stars = emptyList())
            repoId = fireStore.collection(REPOSITORY_COLLECTION).add(repo).await().id
        } else {
            repoId = query.documents.first().id
        }

        val listener = fireStore.collection(REPOSITORY_COLLECTION).document(repoId)
            .addSnapshotListener{ snapshot, e ->
                if (e != null) {
                    Log.e("Repository", e.message.toString())
                    close(e)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val repo = snapshot.toObject(Repository::class.java) ?: Repository()
                    trySend(repo)
                }
            }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    suspend fun starRepo(path: String) {
        try {

            fireStore.collection(STARRED_REPOSITORY).document(currentUserId).update("repositories", FieldValue.arrayUnion(path)).await()
            fireStore.collection(REPOSITORY_COLLECTION).document(repoId).update("stars", FieldValue.arrayUnion(currentUserId)).await()

        } catch (e: Exception) {
            Log.e("Repository", e.message.toString())
        }
    }

    suspend fun unstarRepo(path: String) {
        try {

            fireStore.collection(STARRED_REPOSITORY).document(currentUserId).update("repositories", FieldValue.arrayRemove(path)).await()
            fireStore.collection(REPOSITORY_COLLECTION).document(repoId).update("stars", FieldValue.arrayRemove(currentUserId))

        } catch (e: Exception) {
            Log.e("Repository", e.message.toString())
        }
    }

    fun getStarredRepositories(userId: String = currentUserId): Flow<StarredRepositories> = callbackFlow {
        val listener = fireStore.collection(STARRED_REPOSITORY).document(userId)
            .addSnapshotListener { result, e ->
                if (e != null) {
                    Log.e("RepositoryData", "Error fetching repositories: ${e.message}")
                    close(e)
                    return@addSnapshotListener
                }
                if (result != null) {
                    val starRepo = result.toObject(StarredRepositories::class.java) ?: StarredRepositories()
                    trySend(starRepo)
                }
            }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun fetchExplorerRepositories(friends: Friends) {
        val followers = (friends.followers + friends.following).distinctBy { it }
        var followedRepos: List<Repository> = emptyList()
        fetchFollowedRepositories(followers) {
            followedRepos = it
        }
        fireStore.collection(REPOSITORY_COLLECTION)
            .whereEqualTo("private", false)
            .orderBy("stars", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { results ->
                val trendingRepos = results.toObjects(Repository::class.java)
                val repos = (trendingRepos + followedRepos).distinctBy { it.id }.shuffled()
                _exploreRepositories.value = repos
            }.addOnFailureListener {
                Log.e("ExploreFragment", it.message.toString())
            }
    }

    private fun fetchFollowedRepositories(friends: List<String>, callback: (List<Repository>) -> Unit) {
        if (friends.isEmpty()) {
            callback(emptyList())
            return
        }
        fireStore.collection(USER_COLLECTION).whereIn("userId", friends).get().addOnSuccessListener { result ->
            val followedUsernames = result.documents.mapNotNull { it.getString("userName") }
            fireStore.collection(REPOSITORY_COLLECTION)
                    .whereEqualTo("private", false)
                    .whereIn("owner", followedUsernames)
                    .get()
                    .addOnSuccessListener { results ->
                        val repo = results.toObjects(Repository::class.java)
                        callback(repo)
                    }.addOnFailureListener { e ->
                        Log.e("ExploreFragment", e.message.toString())
                    }
        }.addOnFailureListener { e ->
            Log.e("ExploreFragment", e.message.toString())
        }
    }
}