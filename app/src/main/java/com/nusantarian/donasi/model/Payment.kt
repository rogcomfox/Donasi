package com.nusantarian.donasi.model

import android.view.View
import android.widget.TextView
import com.google.common.base.Verify
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.rpc.context.AttributeContext
import com.nusantarian.donasi.R
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.text.DecimalFormat

data class Payment (
    val userUID: String,
    val donationUID: String,
    val donation: Int,
    val uniqueCode: Int,
    val bank: String,
    val transferDate: String,
    var verified: Boolean,
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

class UserHistoryPayment(val payment: Payment, val donation: Donation, val key: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.tv_title).text = donation.title
        val formattedPrice = DecimalFormat("#,###").format(payment.donation + payment.uniqueCode)
        viewHolder.itemView.findViewById<TextView>(R.id.tv_donation).text = "Rp " + formattedPrice.toString()
        viewHolder.itemView.findViewById<TextView>(R.id.tv_status).text = if (payment.verified) "DONE" else "WAITING"

        if(payment.verified){
            viewHolder.itemView.findViewById<TextView>(R.id.tv_status).setBackgroundResource(R.drawable.rounded_status_verified)
        }else{
            viewHolder.itemView.findViewById<TextView>(R.id.tv_status).setBackgroundResource(R.drawable.rounded_status_unverified)
        }
    }

    fun getItem(): Payment {
        return payment
    }

    override fun getLayout(): Int {
        return R.layout.item_payment_user_history
    }

}

class AdminDetailPayment(val payment: Payment, val user: User, val key: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {

        viewHolder.itemView.findViewById<TextView>(R.id.tv_admDetail_name).text = user.name
        viewHolder.itemView.findViewById<TextView>(R.id.tv_admDetail_bank).text = payment.bank.removeSuffix("Transfer")
        val formattedPrice = DecimalFormat("#,###").format(payment.donation + payment.uniqueCode)
        viewHolder.itemView.findViewById<TextView>(R.id.tv_admDetail_donationTotal).text = "Rp $formattedPrice"
        viewHolder.itemView.findViewById<TextView>(R.id.tv_admDetail_transferDate).text = payment.transferDate

        viewHolder.itemView.findViewById<TextView>(R.id.tv_admDetail_status).text = if (payment.verified) "VERIFIED" else "NOT VERIFIED"

        if(payment.verified){
            viewHolder.itemView.findViewById<TextView>(R.id.tv_admDetail_status).setBackgroundResource(R.drawable.rounded_status_verified)
        }else{
            viewHolder.itemView.findViewById<TextView>(R.id.tv_admDetail_status).setBackgroundResource(R.drawable.rounded_status_unverified)
        }
    }

    fun setVerification(view: View, status: Boolean){
        payment.verified = status

        view.findViewById<TextView>(R.id.tv_admDetail_status).text = if (payment.verified) "VERIFIED" else "NOT VERIFIED"

        if(payment.verified){
            view.findViewById<TextView>(R.id.tv_admDetail_status).setBackgroundResource(R.drawable.rounded_status_verified)
        }else{
            view.findViewById<TextView>(R.id.tv_admDetail_status).setBackgroundResource(R.drawable.rounded_status_unverified)
        }
    }

    fun getItem(): Payment {
        return payment
    }

    override fun getLayout(): Int {
        return R.layout.item_payment_admin_detail
    }

}