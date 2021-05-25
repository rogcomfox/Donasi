package com.nusantarian.donasi.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

data class User(
    val email: String,
    val name: String,
    val phone: String,
    val profileURL: String
){
    companion object{
        fun docToUser(document: QueryDocumentSnapshot): User{

            return User(
                document["email"].toString(),
                document["name"].toString(),
                document["phone"].toString(),
                document["profileURL"].toString()
            )
        }

        fun docToUser(document: DocumentSnapshot): User{

            return User(
                document["email"].toString(),
                document["name"].toString(),
                document["phone"].toString(),
                document["profileURL"].toString()
            )
        }
    }
}