package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentDonateBinding
import com.nusantarian.donasi.databinding.FragmentHomeUserBinding
import com.nusantarian.donasi.model.Donation
import com.nusantarian.donasi.model.HomeDonation
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

class DonateFragment : Fragment() {

    private var _binding: FragmentDonateBinding? = null
    private val binding get() = _binding!!
    private val args: DonateFragmentArgs by navArgs()

    val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDonateBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()

        binding.btnContinue.setOnClickListener {
            if (binding.spPayment.selectedItem != "" && binding.etDonate.text.toString()
                    .toInt() >= 10000
            ) {

                Toast.makeText(
                    getActivity(),
                    "Success.",
                    Toast.LENGTH_SHORT
                ).show()

                val date = SimpleDateFormat("yyyy-MM-dd").format(LocalDate.now())

                val payment = hashMapOf(
                    "userUID" to auth.currentUser.uid,
                    "donationUID" to args.donationUID,
                    "donation" to binding.etDonate.text.toString().toInt(),
                    "bank" to binding.spPayment.selectedItem.toString(),
                    "transferDate" to date,
                    "verified" to false
                )

                db.collection("cities").document("LA")
                    .set(city)
                    .addOnSuccessListener { }
                    .addOnFailureListener { }


            } else {
                Toast.makeText(
                    getActivity(),
                    "Please enter all fields correctly.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadData() {
        val donationDoc = db.collection("donations").document(args.donationUID)
        donationDoc.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val donation = Donation.docToDonation(document)

                    binding.tvTitle.text = donation.title

                } else {
                }
            }


        //Setup spinner start
        val spinner = binding.spPayment
        val items = mutableListOf<String>(
            "BNI Syariah Transfer",
            "BNI Transfer",
            "BRI Transfer",
            "Mandiri Transfer",
            "BCA Transfer"
        )
        items.sort()
        items.add("")
        val adapter = view?.let {
            ArrayAdapter<String>(
                it.context,
                android.R.layout.simple_spinner_dropdown_item,
                items
            )
        }
        spinner.adapter = adapter
        spinner.setSelection(items.lastIndex)
        //Setup spinner end
    }
}