package com.nusantarian.donasi.model

import java.util.*

data class Donation(
    val title: String,
    val desc: String,
    val startDate: Date,
    val deadlineDate: Date,
    val cashCollected: Int,
    val cashTarget: Int,
    val donorQty: Int
)