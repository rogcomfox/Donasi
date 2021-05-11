package com.nusantarian.donasi.model

import java.util.*

data class Donation(
    val title: String,
    val desc: String,

    //-----yyyymmdd---------
    val startDate: String,
    val deadlineDate: String,
    //----------------------

    val cashCollected: Int,
    val cashTarget: Int,

    //jumlah donor
    val donorQty: Int
)