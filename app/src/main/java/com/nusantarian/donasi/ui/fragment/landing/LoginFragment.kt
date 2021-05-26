package com.nusantarian.donasi.ui.fragment.landing

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentLoginBinding
import com.nusantarian.donasi.ui.activity.AdminMainActivity
import com.nusantarian.donasi.ui.activity.MainActivity
import com.nusantarian.donasi.util.Helper

class LoginFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val registerInfo = requireActivity().resources.getString(R.string.tv_register_direction)
        val register = requireActivity().resources.getString(R.string.tv_register)
        val color = Helper().getColoredSpanned(register, "#80B996")
        binding.tvRegisterDirection.text =
            Html.fromHtml("$registerInfo $color", Html.FROM_HTML_MODE_LEGACY)

        //onClick Listener
        binding.btnLogin.setOnClickListener(this)
        binding.tvForgotPass.setOnClickListener(this)
        binding.tvRegisterDirection.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_register_direction ->
                findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment(null))
            R.id.tv_forgot_pass ->
                findNavController()
                    .navigate(LoginFragmentDirections.actionLoginFragmentToForgotPassFragment())
            R.id.btn_login -> loginAccount()
        }
    }

    private fun loginAccount() {
        binding.progress.visibility = View.VISIBLE
        val email = binding.tilEmail.editText?.text.toString()
        val pass = binding.tilPass.editText?.text.toString()
        val auth = FirebaseAuth.getInstance()
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                binding.progress.visibility = View.GONE
                if (ADMIN_UID.contains(auth.currentUser?.uid))
                    startActivity(Intent(requireContext(), AdminMainActivity::class.java))
                else
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finishAffinity()
            }
        }.addOnFailureListener {
            binding.progress.visibility = View.GONE
            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val ADMIN_UID = mutableListOf(
            "5JpWS9ze6tSlPvSjHj8njYFws533",
            "f0Revm2pNff226Uxi6QzEK1KPq43"
        )
    }
}