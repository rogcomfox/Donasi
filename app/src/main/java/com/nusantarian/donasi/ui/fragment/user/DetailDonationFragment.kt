package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentDetailDonationBinding
import com.nusantarian.donasi.model.Donation
import java.text.DecimalFormat
import java.time.LocalDate

class DetailDonationFragment : Fragment() {

    private var _binding: FragmentDetailDonationBinding? = null
    private val binding get() = _binding!!
    private val args:DetailDonationFragmentArgs by navArgs()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailDonationBinding.inflate(inflater, container, false)
        //set toolbar
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData(){

        val donationDoc = db.collection("donations").document(args.donationUID)
        donationDoc.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val donation = Donation.docToDonation(document)

                    binding.tvDetailTitle.text = donation.title
                    var formattedPrice = DecimalFormat("#,###").format(donation.cashCollected)
                    binding.tvCashCollected.text = "Rp $formattedPrice"
                    formattedPrice = DecimalFormat("#,###").format(donation.cashTarget)
                    binding.tvCashTarget.text = "collected from Rp $formattedPrice"
                    binding.pbCash.max = donation.cashTarget
                    binding.pbCash.progress = donation.cashCollected

                    binding.tvDetailDuration.text = donation.currentDuration().toString() + " days remaining";
                    binding.tvDonorQty.text = donation.donorQty.toString() + " donations";
                    binding.tvStartDate.text = donation.startDate;

                    binding.tvDetailDesc.text = donation.desc;

                    binding.btnDonate.setOnClickListener {
                        findNavController()
                            .navigate(
                                DetailDonationFragmentDirections.actionDetailDonationFragmentToDonateFragment(args.donationUID)
                            )

                    }

                }
            }


    }
}