package com.nusantarian.donasi.model

import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nusantarian.donasi.R
import com.nusantarian.donasi.ui.fragment.admin.MainAdminFragmentDirections
import com.nusantarian.donasi.ui.fragment.user.HomeUserFragmentDirections
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.text.DecimalFormat
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
) {
    companion object {
        fun docToDonation(document: QueryDocumentSnapshot): Donation {

            return Donation(
                document["title"].toString(),
                document["desc"].toString(),
                document["startDate"].toString(),
                document["deadlineDate"].toString(),
                document["cashCollected"].toString().toInt(),
                document["cashTarget"].toString().toInt(),
                document["donorQty"].toString().toInt()
            )
        }

        fun docToDonation(document: DocumentSnapshot): Donation {

            return Donation(
                document["title"].toString(),
                document["desc"].toString(),
                document["startDate"].toString(),
                document["deadlineDate"].toString(),
                document["cashCollected"].toString().toInt(),
                document["cashTarget"].toString().toInt(),
                document["donorQty"].toString().toInt()
            )
        }
    }

    fun totalDuration(): Int {
        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(deadlineDate)

        return ChronoUnit.DAYS.between(start, end).toInt()
    }

    fun currentDuration(): Int {
        val start = LocalDate.now()
        val end = LocalDate.parse(deadlineDate)

        return ChronoUnit.DAYS.between(start, end).toInt()
    }

    fun keywords(): String = (title + desc + startDate + deadlineDate).lowercase()
}

class HomeDonation(val donation: Donation, val key: String, val fragment: Fragment) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_title).text = donation.title
        viewHolder.itemView.findViewById<TextView>(R.id.tv_desc).text = donation.desc
        viewHolder.itemView.findViewById<TextView>(R.id.tv_duration).text =
            donation.currentDuration().toString() + " days remaining"


        viewHolder.itemView.findViewById<MaterialButton>(R.id.btn_donate).setOnClickListener {
            findNavController(fragment)
                .navigate(HomeUserFragmentDirections.actionHomeUserFragmentToDonateFragment(key))
        }

    }

    fun getItem(): Donation = donation

    override fun getLayout(): Int = R.layout.item_donation_home
}

class HomeAdminDonation(val donation: Donation, private val key: String, val fragment: Fragment) :
    Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_title).text = donation.title
        viewHolder.itemView.findViewById<TextView>(R.id.tv_desc).text = donation.desc
        viewHolder.itemView.findViewById<TextView>(R.id.tv_duration).text =
            donation.currentDuration().toString() + " days remaining"


        viewHolder.itemView.findViewById<MaterialButton>(R.id.btn_donate).setOnClickListener {
            findNavController(fragment)
                .navigate(
                    MainAdminFragmentDirections.actionMainAdminFragmentToEditDonationFragment(
                        key
                    )
                )
        }

    }

    fun getItem(): Donation = donation

    override fun getLayout(): Int = R.layout.item_donation_home_admin
}

class OrganizerDonation(val donation: Donation, val key: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_org_title).text = donation.title
        var formattedPrice = DecimalFormat("#,###").format(donation.cashCollected)
        viewHolder.itemView.findViewById<TextView>(R.id.tv_org_collected).text =
            "Rp $formattedPrice"
        formattedPrice = DecimalFormat("#,###").format(donation.cashTarget)
        viewHolder.itemView.findViewById<TextView>(R.id.tv_org_target).text =
            "collected from Rp $formattedPrice"
        viewHolder.itemView.findViewById<ProgressBar>(R.id.pb_org).max = donation.cashTarget
        viewHolder.itemView.findViewById<ProgressBar>(R.id.pb_org).progress = donation.cashCollected

        viewHolder.itemView.findViewById<TextView>(R.id.tv_org_donorQty).text =
            "${donation.donorQty} donations"
        viewHolder.itemView.findViewById<TextView>(R.id.tv_org_duration).text =
            "${donation.currentDuration()} days remaining"
    }

    fun getItem(): Donation = donation

    override fun getLayout(): Int = R.layout.item_donation_organizer
}