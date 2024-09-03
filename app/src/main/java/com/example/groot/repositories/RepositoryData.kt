package com.example.groot.repositories

import android.util.Log
import com.example.groot.model.Repository
import com.example.groot.model.StarredRepositories
import com.example.groot.utility.REPOSITORY_COLLECTION
import com.example.groot.utility.STARRED_REPOSITORY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class RepositoryData {

    private val auth = FirebaseAuth.getInstance()
    private val fireStore = FirebaseFirestore.getInstance()
    val currentUserId get() = auth.currentUser?.uid ?: ""

    private var repoId = ""

    private val _repository = MutableStateFlow(Repository())
    val repository: StateFlow<Repository> get() =  _repository

    private val _starredRepositories = MutableStateFlow(StarredRepositories())
    val starredRepositories: StateFlow<StarredRepositories> get() = _starredRepositories

    suspend fun getRepository (path: String) {
        val owner = path.substringBefore("/").trim()
        val repoName = path.substringAfter("/").trim()
        val query = fireStore.collection(REPOSITORY_COLLECTION).whereEqualTo("name", repoName).whereEqualTo("owner", owner).get().await()
        if (query.documents.isEmpty()) {
            val repo = Repository(name = repoName, owner = owner, private = false, stars = emptyList())
            repoId = fireStore.collection(REPOSITORY_COLLECTION).add(repo).await().id
            _repository.value = repo.copy(id = repoId)
        } else {
            repoId = query.documents.first().id
        }
        val docRef = fireStore.collection(REPOSITORY_COLLECTION).document(repoId)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Repository", e.message.toString())
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                _repository.value = snapshot.toObject(Repository::class.java) ?: Repository()
            }
        }
    }

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

    fun getStarredRepositories(userId: String = currentUserId) {
        val docRef = fireStore.collection(STARRED_REPOSITORY).document(userId)
        docRef.get().addOnSuccessListener {
            if (!it.exists()) {
                val star = StarredRepositories(userId = currentUserId, repositories = emptyList())
                docRef.set(star)
                _starredRepositories.value = star
            }
        }
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
