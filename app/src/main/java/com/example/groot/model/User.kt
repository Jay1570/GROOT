
package com.example.groot.model
import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val userId: String = "",
    val email: String = "",
    val imgUrl: String = "",
    val userName: String = ""
)