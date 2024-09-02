package com.example.groot.model

import com.google.firebase.firestore.DocumentId

data class StarredRepositories(
    @DocumentId val userId: String = "",
    val repositories: List<String> = emptyList()
)
