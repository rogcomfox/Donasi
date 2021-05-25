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
import com.google.firebase.Timestamp
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
import kotlin.random.Random

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
    }

    private fun loadData() {
        val donationDoc = db.collection("donations").document(args.donationUID)
        donationDoc.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val donation = Donation.docToDonation(document)

                    binding.tvTitle.text = donation.title

                    //Setup spinner start
                    val spinner = binding.spPayment
                    val items = PAYMENT_OPTIONS
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

                    binding.btnContinue.setOnClickListener {
                        if (binding.spPayment.selectedItem != "" && binding.etDonate.text.toString()
                                .toInt() >= 10000
                        ) {

                            val date = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
                            val uniqueCode = Random.nextInt(1, 999)
                            val timestamp = Timestamp.now().toDate()

                            val payment = hashMapOf(
                                "userUID" to auth.currentUser?.uid,
                                "donationUID" to args.donationUID,
                                "donation" to binding.etDonate.text.toString().toInt(),
                                "uniqueCode" to uniqueCode,
                                "bank" to binding.spPayment.selectedItem.toString(),
                                "transferDate" to date,
                                "verified" to false,
                                "invoiceURL" to "",
                                "timestamp" to timestamp
                            )

                            val paymentUID = UUID.randomUUID().toString()

                            db.collection("payments").document(paymentUID)
                                .set(payment)
                                .addOnSuccessListener {
                                    db.collection("users")
                                        .document(auth.currentUser?.uid!!)
                                        .collection("mydonations")
                                        .document(paymentUID)
                                        .set(hashMapOf("timestamp" to timestamp))
                                        .addOnSuccessListener {
                                            findNavController()
                                                .navigate(
                                                    DonateFragmentDirections.actionDonateFragmentToPaymentInstructionFragment(paymentUID)
                                                )
                                        }
                                }
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
            }



    }

    companion object {
        val PAYMENT_OPTIONS = mutableListOf<String>(
            "BNI Syariah Transfer",
            "BNI Transfer",
            "BRI Transfer",
            "Mandiri Transfer",
            "BCA Transfer"
        )

        val ACCOUNT_NAME = hashMapOf(
            "BNI Syariah Transfer" to "Yayasan Donasi BNS",
            "BNI Transfer" to "Yayasan Donasi BN",
            "BRI Transfer" to "Yayasan Donasi BR",
            "Mandiri Transfer" to "Yayasan Donasi MA",
            "BCA Transfer" to "Yayasan Donasi BC"
        )

        val ACCOUNT_NO = hashMapOf(
            "BNI Syariah Transfer" to "8800123567",
            "BNI Transfer" to "8801123567",
            "BRI Transfer" to "8802123567",
            "Mandiri Transfer" to "8803123567",
            "BCA Transfer" to "8804123567"
        )
    }
}