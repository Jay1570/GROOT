package com.example.groot.model

import com.google.firebase.firestore.DocumentId

data class Repository(
    @DocumentId val id: String = "",
    val name: String = "",
    val owner: String = "",
    val isPrivate: Boolean = false,
)