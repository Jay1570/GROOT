package com.example.groot.model

import com.google.firebase.firestore.DocumentId

data class Following(
    @DocumentId val userId: String = "",
    val following: List<String> = emptyList()
)