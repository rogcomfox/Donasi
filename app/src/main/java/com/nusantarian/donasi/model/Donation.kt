package com.nusantarian.donasi.model

import android.widget.TextView
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nusantarian.donasi.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.time.LocalDate
import java.time.temporal.ChronoUnit


data class Donation(
    val title: String,
    val desc: String,

    //-----yyyy-MM-dd---------
    val startDate: String,
    val deadlineDate: String,
    //----------------------

    val cashCollected: Int,
    val cashTarget: Int,

    //jumlah donor
    val donorQty: Int
){
    companion object{
        fun docToDonation(document: QueryDocumentSnapshot): Donation{
            var donation: Donation = Donation(
                document["title"].toString(),
                document["desc"].toString(),
                document["startDate"].toString(),
                document["deadlineDate"].toString(),
                document["cashCollected"].toString().toInt(),
                document["cashTarget"].toString().toInt(),
                document["donorQty"].toString().toInt()
            )

            return donation
        }
    }

    fun duration() : Int{
        val start: LocalDate = LocalDate.parse(startDate)
        val end: LocalDate = LocalDate.parse(deadlineDate)

        return ChronoUnit.DAYS.between(start, end).toInt()
    }
}

class HomeDonation(val donation: Donation, val key: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_title).text = donation.title
        viewHolder.itemView.findViewById<TextView>(R.id.tv_desc).text = donation.desc
        viewHolder.itemView.findViewById<TextView>(R.id.tv_duration).text = "Closes after " + donation.duration().toString() + " days"
    }

    fun getItem(): Donation {
        return donation
    }

    override fun getLayout(): Int {
        return R.layout.item_donation_home
    }

}