package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.nusantarian.donasi.databinding.FragmentHistoryUserBinding
import com.nusantarian.donasi.model.Donation
import com.nusantarian.donasi.model.HomeDonation
import com.nusantarian.donasi.model.Payment
import com.nusantarian.donasi.model.UserHistoryPayment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class HistoryUserFragment : Fragment() {

    private var _binding: FragmentHistoryUserBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData() {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvMyDonations.layoutManager = linearLayoutManager

        val donationOrder = mutableListOf<String>()
        val orderedPayments = hashMapOf<String, UserHistoryPayment>()

        //Iterate paymentUID milik User
        db.collection("users").document(auth.currentUser?.uid!!).collection("mydonations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                for (myDonationDocument in result) {

                    donationOrder.add(myDonationDocument.id)
                    orderedPayments[myDonationDocument.id] = UserHistoryPayment(
                        Payment("", "", 0, 0, "", "", false, ""),
                        Donation("", "", "", "", 0, 0, 0),
                        "KEY"
                    )

                    //Ambil individu payment dari iterasi, di dalam payment ada donationUID & userUID
                    db.collection("payments").document(myDonationDocument.id).get()
                        .addOnSuccessListener { paymentDocument ->
                            if (paymentDocument != null) {
                                val payment = Payment.docToPayment(paymentDocument)

                                //Ambil individu donation dari payment
                                db.collection("donations")
                                    .document(paymentDocument["donationUID"].toString()).get()
                                    .addOnSuccessListener { donationDocument ->
                                        if (donationDocument != null) {
                                            val donation = Donation.docToDonation(donationDocument)

                                            orderedPayments[myDonationDocument.id] =
                                                UserHistoryPayment(
                                                    payment,
                                                    donation,
                                                    paymentDocument.id
                                                )

                                            val adapter = GroupAdapter<GroupieViewHolder>()
                                            for (donation in donationOrder) {
                                                adapter.add(orderedPayments[donation]!!)
                                            }

                                            adapter.setOnItemClickListener { item, view ->
                                                val d = item as UserHistoryPayment

                                                findNavController()
                                                    .navigate(
                                                        HistoryUserFragmentDirections.actionHistoryUserFragmentToPaymentInstructionFragment(
                                                            item.key
                                                        )
                                                    )
                                            }

                                            binding.rvMyDonations.adapter = adapter

                                        }
                                    }

                                //Ambil individu user dari payment
                                /*db.collection("donations").document(paymentDocument["userUID"].toString()).get()
                                    .addOnSuccessListener { userDocument ->
                                        if (userDocument != null) {
                                            val user = User.docToUser(userDocument)

                                        }
                                    }
                                */
                            }
                        }
                }
            }

        /*
        How to handle race condition with firebase network latency and looping
        1. get urutan yg bener (line 60)
        2. set data template in correct order (line 61)
        3. put data in the correct order, use index (line 77)
        3. load and destroy each iteration (line 89-93)
        */

    }
}