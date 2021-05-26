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
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentAddDonationBinding
import com.nusantarian.donasi.model.Donation
import java.util.*

class AddDonationFragment : Fragment() {

    private var _binding: FragmentAddDonationBinding? = null
    private val binding get() = _binding!!
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddDonationBinding.inflate(inflater, container, false)
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
        binding.etStartDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.etStartDate.setText(
                        resources.getString(
                            R.string.text_date,
                            year,
                            month,
                            day
                        )
                    )
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
        binding.etEndDate.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    binding.etEndDate.setText(
                        resources.getString(
                            R.string.text_date,
                            year,
                            month,
                            day
                        )
                    )
                },
                calendar[Calendar.YEAR],
                calendar[Calendar.MONTH],
                calendar[Calendar.DAY_OF_MONTH]
            ).show()
        }
        binding.btnStartFund.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            val title = binding.tilTitle.editText?.text.toString()
            val start = binding.tilStartDate.editText?.text.toString()
            val end = binding.tilEndDate.editText?.text.toString()
            val target = binding.tilTarget.editText?.text.toString()
            val story = binding.tilStory.editText?.text.toString()

            if (!isValidInput(title, start, end, target, story))
                binding.progress.visibility = View.GONE
            else
                createDonation(title, start, end, target, story)
        }
    }

    private fun createDonation(
        title: String,
        start: String,
        end: String,
        target: String,
        story: String
    ) {
        val db = FirebaseFirestore.getInstance().collection("donations").document()
        val model = Donation(title, story, start, end, 0, target.toInt(), 0)
        db.set(model).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Success Create Donation", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            } else {
                Toast.makeText(context, "Failed Create Donation", Toast.LENGTH_SHORT).show()
            }
            binding.progress.visibility = View.GONE
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            binding.progress.visibility = View.GONE
        }
    }

    private fun isValidInput(
        title: String,
        start: String,
        end: String,
        target: String,
        story: String
    ): Boolean {
        val empty = resources.getString(R.string.text_empty)
        return when {
            title.isEmpty() -> {
                binding.tilTitle.error = empty
                false
            }
            start.isEmpty() -> {
                binding.tilStartDate.error = empty
                false
            }
            end.isEmpty() -> {
                binding.tilEndDate.error = empty
                false
            }
            target.isEmpty() -> {
                binding.tilTarget.error = empty
                false
            }
            story.isEmpty() -> {
                binding.tilStory.error = empty
                false
            }
            else -> {
                binding.tilTitle.error = null
                binding.tilStartDate.error = null
                binding.tilEndDate.error = null
                binding.tilTarget.error = null
                binding.tilStory.error = null

                binding.tilTitle.isErrorEnabled
                binding.tilTitle.isErrorEnabled
                binding.tilTitle.isErrorEnabled
                binding.tilTitle.isErrorEnabled
                binding.tilTitle.isErrorEnabled
                true
            }
        }
    }
}