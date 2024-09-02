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

    private val _repository = MutableStateFlow(Repository())
    val repository: StateFlow<Repository> get() =  _repository


    suspend fun getRepository (path: String) {
        val owner = path.substringBefore("/").trim()
        val repoName = path.substringAfter("/").trim()
        val query = fireStore.collection(REPOSITORY_COLLECTION).whereEqualTo("name", repoName).whereEqualTo("owner", owner).get().await()
        if (query.documents.isEmpty()) {
            _repository.value = Repository()
            return
        }
        val repoId = query.documents.first().id
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
        val owner = path.substringBefore("/").trim()
        val repoName = path.substringAfter("/").trim()
        try {

            val docRef = fireStore.collection(STARRED_REPOSITORY).document(currentUserId)
            val document = docRef.get().await()

            if (document.exists()) {
                docRef.update("repositories", FieldValue.arrayUnion(path)).await()
            } else {
                val star = StarredRepositories(userId = currentUserId, repositories = listOf(path))
                docRef.set(star).await()
            }

            val query = fireStore.collection(REPOSITORY_COLLECTION).whereEqualTo("name", repoName).whereEqualTo("owner", owner)
            val repoDocument = query.get().await()

            if (repoDocument.isEmpty) {
                val repo = Repository(name = repoName, owner = owner, stars = listOf(currentUserId))
                fireStore.collection(REPOSITORY_COLLECTION).add(repo).await()
                _repository.value = repo
            } else {
                val docId = repoDocument.documents.first().id
                fireStore.collection(REPOSITORY_COLLECTION).document(docId).update("stars", FieldValue.arrayUnion(currentUserId)).await()
            }

        } catch (e: Exception) {
            Log.e("Repository", e.message.toString())
        }
    }

    suspend fun unstarRepo(path: String) {
        try {
            val repoName = path.substringAfter("/").trim()
            val owner = path.substringBefore("/").trim()
            fireStore.collection(STARRED_REPOSITORY).document(currentUserId)
                .update("repositories", FieldValue.arrayRemove(path)).await()
            val repoDocument = fireStore
                .collection(REPOSITORY_COLLECTION)
                .whereEqualTo("name", repoName)
                .whereEqualTo("owner", owner)
                .get()
                .await().documents.first().id

            fireStore.collection(REPOSITORY_COLLECTION).document(repoDocument).update("stars", FieldValue.arrayRemove(currentUserId))
        } catch (e: Exception) {
            Log.e("Repository", e.message.toString())
        }
    }
}
