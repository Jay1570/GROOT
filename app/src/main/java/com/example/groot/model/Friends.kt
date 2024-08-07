package com.example.groot.model

import com.google.firebase.firestore.DocumentId

data class Friends(
    @DocumentId val userId: String = "",
    val following: List<String> = emptyList(),
    val followers: List<String> = emptyList()
)