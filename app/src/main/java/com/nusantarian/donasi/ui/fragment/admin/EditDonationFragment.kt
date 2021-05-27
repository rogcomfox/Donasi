package com.nusantarian.donasi.ui.fragment.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.databinding.FragmentEditDonationBinding
import java.text.SimpleDateFormat
import java.util.*

class EditDonationFragment : Fragment() {

    private var _binding: FragmentEditDonationBinding? = null
    private val binding get() = _binding!!
    private val args: EditDonationFragmentArgs by navArgs()
    private val db = FirebaseFirestore.getInstance().collection("donations")
    private val calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditDonationBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
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

        val dateStart = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.etStartDate.setText(sdf.format(calendar.time))
        }
        val dateEnd = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            binding.etEndDate.setText(sdf.format(calendar.time))
        }
        binding.etStartDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateStart,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
        binding.etEndDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                dateEnd,
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        binding.btnSave.setOnClickListener {
            updateData()
        }
    }

    private fun updateData() {
        val title = binding.tilTitle.editText?.text.toString()
        val start = binding.tilStartDate.editText?.text.toString()
        val end = binding.tilEndDate.editText?.text.toString()
        val story = binding.tilStory.editText?.text.toString()
        db.document(args.donationUID).update(
            mapOf(
                "title" to title,
                "startDate" to start,
                "deadlineDate" to end,
                "desc" to story
            )
        ).addOnCompleteListener {
            if (it.isSuccessful)
                Toast.makeText(context, "Success Update Data", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(context, "Failed Update Data", Toast.LENGTH_SHORT).show()
            binding.progress.visibility = View.GONE
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            binding.progress.visibility = View.GONE
        }
    }

    private fun loadData() {
        binding.progress.visibility = View.VISIBLE
        db.document(args.donationUID).get().addOnSuccessListener {
            binding.tilTitle.editText?.setText(it.getString("title"))
            binding.tilStartDate.editText?.setText(it.getString("startDate"))
            binding.tilEndDate.editText?.setText(it.getString("deadlineDate"))
            binding.tilTarget.editText?.setText(it.get("cashTarget").toString())
            binding.tilStory.editText?.setText(it.getString("desc"))
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
        binding.progress.visibility = View.GONE
    }
}