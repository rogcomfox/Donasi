package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentDetailDonationBinding
import com.nusantarian.donasi.model.Donation
import java.text.DecimalFormat

class DetailDonationFragment : Fragment() {

    private var _binding: FragmentDetailDonationBinding? = null
    private val binding get() = _binding!!
    private val args:DetailDonationFragmentArgs by navArgs()
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

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

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setDisplay(view)
    }

    private fun setDisplay(view: View){

        val donationDoc = db.collection("donations").document(args.donationUID)
        donationDoc.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val donation = Donation.docToDonation(document)

                    view.findViewById<TextView>(R.id.tv_detail_title).text = donation.title
                    var formattedPrice = DecimalFormat("#,###").format(donation.cashCollected)
                    view.findViewById<TextView>(R.id.tv_cashCollected).text = "Rp $formattedPrice"
                    formattedPrice = DecimalFormat("#,###").format(donation.cashTarget)
                    view.findViewById<TextView>(R.id.tv_cashTarget).text = "collected from Rp $formattedPrice"
                    view.findViewById<ProgressBar>(R.id.pb_cash).max = donation.cashTarget
                    view.findViewById<ProgressBar>(R.id.pb_cash).progress = donation.cashCollected

                    view.findViewById<TextView>(R.id.tv_detail_duration).text = donation.currentDuration().toString() + " days remaining";
                    view.findViewById<TextView>(R.id.tv_donorQty).text = donation.donorQty.toString() + " donations";
                    view.findViewById<TextView>(R.id.tv_startDate).text = donation.startDate;

                    view.findViewById<TextView>(R.id.tv_detail_desc).text = donation.desc;

                } else {
                }
            }
    }
}