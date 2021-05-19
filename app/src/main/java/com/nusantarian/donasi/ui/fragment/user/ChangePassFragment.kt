package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.nusantarian.donasi.databinding.FragmentChangePassBinding

class ChangePassFragment : Fragment() {

    private var _binding: FragmentChangePassBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChangePassBinding.inflate(inflater, container, false)
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
        resetPass()
    }

    private fun resetPass() {
        binding.progress.visibility = View.VISIBLE
        val new = binding.tilNewPass.editText?.text.toString()
        val confirm = binding.tilConfirm.editText?.text.toString()
        val auth = FirebaseAuth.getInstance().currentUser!!

        if (new != confirm)
            binding.progress.visibility = View.GONE
        else
            auth.updatePassword(new).addOnCompleteListener {
                if (it.isSuccessful)
                    Toast.makeText(context, "Password Success to Update", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(context, "Failed to Update Password", Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
            }.addOnFailureListener {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
            }
    }
}