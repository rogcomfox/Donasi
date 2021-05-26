package com.nusantarian.donasi.ui.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentPaymentOrgBinding
import com.nusantarian.donasi.model.Donation
import com.nusantarian.donasi.model.HomeDonation
import com.nusantarian.donasi.model.OrganizerDonation
import com.nusantarian.donasi.ui.fragment.user.HomeUserFragmentDirections
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class PaymentOrgFragment : Fragment() {
    private var _binding: FragmentPaymentOrgBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPaymentOrgBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData(){
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.rvOrganizer.layoutManager = linearLayoutManager

        val adapter = GroupAdapter<GroupieViewHolder>()


        db.collection("donations")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val donation: Donation = Donation.docToDonation(document)

                    adapter.add(OrganizerDonation(donation, document.id))

                }
            }

        adapter.setOnItemClickListener { item, _ ->
            val donation = item as OrganizerDonation
            findNavController()
                .navigate(
                    PaymentOrgFragmentDirections.actionPaymentOrgFragmentToDetailPaymentFragment(donation.key)
                )
        }

        binding.rvOrganizer.adapter = adapter
    }
}