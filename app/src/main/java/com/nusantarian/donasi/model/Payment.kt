package com.nusantarian.donasi.model

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

data class Payment (
    val userUID: String,
    val donationUID: String,
    val donation: Int,
    val uniqueCode: Int,
    val bank: String,
    val transferDate: String,
    val verified: Boolean,
    val invoiceURL: String
        ){

    companion object{
        fun docToPayment(document: QueryDocumentSnapshot): Payment{

            return Payment(
                document["userUID"].toString(),
                document["donationUID"].toString(),
                document["donation"].toString().toInt(),
                document["uniqueCode"].toString().toInt(),
                document["bank"].toString(),
                document["transferDate"].toString(),
                document["verified"].toString().toBoolean(),
                document["invoiceURL"].toString()
            )
        }

        fun docToPayment(document: DocumentSnapshot): Payment{

            return Payment(
                document["userUID"].toString(),
                document["donationUID"].toString(),
                document["donation"].toString().toInt(),
                document["uniqueCode"].toString().toInt(),
                document["bank"].toString(),
                document["transferDate"].toString(),
                document["verified"].toString().toBoolean(),
                document["invoiceURL"].toString()
            )
        }
    }
}