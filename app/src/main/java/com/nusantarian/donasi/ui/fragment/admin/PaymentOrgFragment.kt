package com.nusantarian.donasi.ui.fragment.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.databinding.FragmentPaymentOrgBinding
import com.nusantarian.donasi.model.Donation
import com.nusantarian.donasi.model.OrganizerDonation
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
                    PaymentOrgFragmentDirections.actionPaymentOrgFragmentToDetailPaymentFragment(
                        donation.key
                    )
                )
        }

        binding.rvOrganizer.adapter = adapter
    }
}