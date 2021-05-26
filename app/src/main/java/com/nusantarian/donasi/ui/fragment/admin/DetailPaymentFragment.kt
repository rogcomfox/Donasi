package com.nusantarian.donasi.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.databinding.FragmentDetailPaymentBinding
import com.nusantarian.donasi.model.AdminDetailPayment
import com.nusantarian.donasi.model.Payment
import com.nusantarian.donasi.ui.fragment.user.DetailDonationFragmentArgs
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class DetailPaymentFragment : Fragment() {

    private var _binding: FragmentDetailPaymentBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val args: DetailDonationFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailPaymentBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                requireActivity().onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
    }

    private fun loadData() {
        binding.progress.visibility = View.VISIBLE
        db.collection("donations").document(args.donationUID).get()
            .addOnSuccessListener {
                binding.tvAdmindetailTitle.text = it["title"].toString()

                val linearLayoutManager = LinearLayoutManager(activity)
                linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
                binding.rvDetailPayment.layoutManager = linearLayoutManager

                val adapter = GroupAdapter<GroupieViewHolder>()

                db.collection("payments").whereEqualTo("donationUID", args.donationUID).get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            db.collection("users").document(document["userUID"].toString()).get()
                                .addOnSuccessListener { userDoc ->
                                    val payment = Payment.docToPayment(document)
                                    val user = com.nusantarian.donasi.model.User.docToUser(userDoc)

                                    val detailPayment =
                                        AdminDetailPayment(payment, user, document.id)

                                    adapter.add(detailPayment)

                                    adapter.setOnItemClickListener { item, view ->
                                        val paymentDetail = item as AdminDetailPayment

                                        binding.tvAdmindetailTitle.text = "Loading..."

                                        if (paymentDetail.getItem().verified) {

                                            db.collection("payments").document(paymentDetail.key)
                                                .update("verified", false).addOnSuccessListener {


                                                    val donationRef = db.collection("donations")
                                                        .document(paymentDetail.getItem().donationUID)

                                                    donationRef.update(
                                                        "cashCollected",
                                                        FieldValue.increment(-(paymentDetail.getItem().donation.toLong()))
                                                    ).addOnSuccessListener {
                                                        donationRef.update(
                                                            "donorQty",
                                                            FieldValue.increment(-1)
                                                        ).addOnSuccessListener {
                                                            paymentDetail.setVerification(
                                                                view,
                                                                false
                                                            )
                                                        }
                                                    }
                                                }
                                        } else {
                                            db.collection("payments").document(paymentDetail.key)
                                                .update("verified", true).addOnSuccessListener {


                                                    val donationRef = db.collection("donations")
                                                        .document(paymentDetail.getItem().donationUID)

                                                    donationRef.update(
                                                        "cashCollected",
                                                        FieldValue.increment(paymentDetail.getItem().donation.toLong())
                                                    ).addOnSuccessListener {
                                                        donationRef.update(
                                                            "donorQty",
                                                            FieldValue.increment(1)
                                                        ).addOnSuccessListener {
                                                            paymentDetail.setVerification(
                                                                view,
                                                                true
                                                            )
                                                        }
                                                    }
                                                }
                                        }
                                    }
                                    binding.rvDetailPayment.adapter = adapter
                                }
                        }
                    }
            }
        binding.progress.visibility = View.GONE
    }

}
