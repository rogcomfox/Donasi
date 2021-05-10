package com.nusantarian.donasi.ui.fragment.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.nusantarian.donasi.databinding.FragmentForgotPassBinding

class ForgotPassFragment : Fragment() {

    private var _binding: FragmentForgotPassBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentForgotPassBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSend.setOnClickListener {
            binding.progress.visibility = View.VISIBLE
            val email = binding.tilEmail.editText.toString()
            val auth = FirebaseAuth.getInstance()
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                binding.progress.visibility = View.GONE
                Toast.makeText(context, "Please Check Your Email Inbox", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                binding.progress.visibility = View.GONE
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}