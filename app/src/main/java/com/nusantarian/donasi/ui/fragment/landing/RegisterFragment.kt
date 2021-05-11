package com.nusantarian.donasi.ui.fragment.landing

import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavArgs
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentRegisterBinding
import com.nusantarian.donasi.model.User

class RegisterFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    //private val args:RegisterFragmentArgs by navArgs()

    companion object {
        const val DEFAULT_PROFILE_PICTURE =
            "https://firebasestorage.googleapis.com/v0/b/donasi-7ddd3.appspot.com/o/profile_pic%2Fdefault%2Fdefault-profile-picture.jpg?alt=media&token=57a0e001-b52d-43b2-b86d-6821256294f2"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnRegister.setOnClickListener(this)
        binding.icBack.setOnClickListener(this)
        //binding.etEmail.setText(args.email)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.ic_back -> requireActivity().onBackPressed()
            R.id.btn_register -> registerAccount()
        }
    }

    private fun registerAccount() {
        binding.progress.visibility = View.VISIBLE
        val email = binding.tilEmail.editText?.text.toString()
        val pass = binding.tilPass.editText?.text.toString()
        val name = binding.tilName.editText?.text.toString()
        val phone = binding.tilPhone.editText?.text.toString()
        val confirm = binding.tilConfirm.editText?.text.toString()
        val auth = FirebaseAuth.getInstance()

        if (!isError(email, name, phone, pass, confirm))
            binding.progress.visibility = View.GONE
        else
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                if (it.isSuccessful) {
                    val id = auth.currentUser?.uid
                    addAccount(email, name, phone, id!!)
                } else {
                    Toast.makeText(context, "Failed to Create Account", Toast.LENGTH_SHORT).show()
                    binding.progress.visibility = View.GONE
                }
            }.addOnFailureListener {
                binding.progress.visibility = View.GONE
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun addAccount(email: String, name: String, phone: String, id: String) {
        val ref = FirebaseFirestore.getInstance().collection("users")
        val user = User(email, name, phone, DEFAULT_PROFILE_PICTURE)
        ref.document(id).set(user).addOnCompleteListener {
            binding.progress.visibility = View.GONE
            Toast.makeText(context, "Please Login First", Toast.LENGTH_SHORT).show()
            findNavController()
                .navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
        }.addOnFailureListener {
            binding.progress.visibility = View.GONE
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun isError(
        email: String,
        name: String,
        phone: String,
        pass: String,
        confirm: String
    ): Boolean {
        val empty = requireActivity().resources.getString(R.string.text_empty)
        val invalid = requireActivity().resources.getString(R.string.text_invalid)
        val notMatch = requireActivity().resources.getString(R.string.text_not_match)

        return when {
            email.isEmpty() -> {
                binding.tilEmail.error = empty
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = invalid
                false
            }
            pass.isEmpty() -> {
                binding.tilPass.error = empty
                false
            }
            pass != confirm -> {
                binding.tilPass.error = notMatch
                binding.tilConfirm.error = notMatch
                false
            }
            phone.isEmpty() -> {
                binding.tilPhone.error = empty
                false
            }
            name.isEmpty() -> {
                binding.tilName.error = empty
                false
            }
            else -> {
                binding.tilEmail.error = null
                binding.tilPhone.error = null
                binding.tilName.error = null
                binding.tilPass.error = null
                binding.tilConfirm.error = null

                binding.tilEmail.isErrorEnabled
                binding.tilPhone.isErrorEnabled
                binding.tilName.isErrorEnabled
                binding.tilPass.isErrorEnabled
                binding.tilConfirm.isErrorEnabled
                true
            }
        }
    }
}