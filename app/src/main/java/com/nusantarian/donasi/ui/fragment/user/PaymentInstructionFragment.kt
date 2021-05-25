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
import com.nusantarian.donasi.databinding.FragmentPaymentInstructionBinding
import com.nusantarian.donasi.model.Donation
import com.nusantarian.donasi.model.HomeDonation
import com.nusantarian.donasi.model.Payment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import java.text.DecimalFormat
import kotlin.random.Random

class PaymentInstructionFragment : Fragment() {

    private var _binding: FragmentPaymentInstructionBinding? = null
    private val binding get() = _binding!!
    private val args: PaymentInstructionFragmentArgs by navArgs()

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPaymentInstructionBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
    }

    private fun loadData() {
        db.collection("payments").document(args.paymentUID).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val payment = Payment.docToPayment(document)

                    val total = payment.uniqueCode + payment.donation

                    var formattedPrice = DecimalFormat("#,###").format(total)
                    binding.tvDonationTotal.text = "Rp $formattedPrice"
                    formattedPrice = DecimalFormat("#,###").format(payment.donation)
                    binding.tvDonationNominal.text = "Rp $formattedPrice"
                    binding.tvDonationUnique.text = payment.uniqueCode.toString()

                    binding.tvAccountName.text = "${payment.bank.removeSuffix("Transfer")}a/n ${DonateFragment.ACCOUNT_NAME[payment.bank]}"
                    binding.tvAccountNumber.text = "${DonateFragment.ACCOUNT_NO[payment.bank]}"

                    binding.btnSeePaymentStatus.setOnClickListener {
                        findNavController()
                            .navigate(
                                PaymentInstructionFragmentDirections.actionPaymentInstructionFragmentToHistoryUserFragment()
                            )
                    }
                }
            }
    }
}