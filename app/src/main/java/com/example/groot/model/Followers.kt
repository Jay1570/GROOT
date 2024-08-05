package com.example.groot.model

import com.google.firebase.firestore.DocumentId

data class Followers(
    @DocumentId val userId: String = "",
    val followers: List<String> = emptyList(),
)