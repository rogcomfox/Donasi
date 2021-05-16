package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()
    private val ref = FirebaseFirestore.getInstance().collection("users")
    private var id = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile_user_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home ->
                requireActivity().onBackPressed()
            R.id.nav_pass ->
                findNavController()
                    .navigate(ProfileFragmentDirections.actionProfileFragmentToChangePassFragment())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        binding.imgProfile.setOnClickListener {
            raiseDialogPicture()
        }
        binding.btnUpdate.setOnClickListener {
            //TODO: sementara ganti gambar ntar aja
            val email = binding.tilEmail.editText?.text.toString()
            val name = binding.tilName.editText?.text.toString()
            val phone = binding.tilPhone.editText?.text.toString()
            updateProfile(email, name, phone)
        }
    }

    private fun updateProfile(email: String, name: String, phone: String) {
        binding.progress.visibility = View.VISIBLE
        auth.currentUser!!.updateEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                ref.document(id).update(
                    mapOf(
                        "email" to email,
                        "name" to name,
                        "phone" to phone
                    )
                ).addOnCompleteListener {
                    Toast.makeText(context, "Success Change Data", Toast.LENGTH_SHORT).show()
                    binding.progress.visibility = View.GONE
                }.addOnFailureListener {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    binding.progress.visibility = View.GONE
                }
            }
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            binding.progress.visibility = View.GONE
        }
    }

    //TODO: Ntar kalo sempet tak tambahin ~ Yusuf
    private fun raiseDialogPicture() {
        Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
    }

    private fun loadData() {
        binding.progress.visibility = View.VISIBLE
        id = auth.currentUser!!.uid
        ref.document(id).get().addOnSuccessListener {
            binding.tilName.editText?.setText(it.getString("name"))
            binding.tilEmail.editText?.setText(it.getString("email"))
            binding.tilPhone.editText?.setText(it.getString("phone"))
            binding.imgProfile.load(it.getString("profileURL")) {
                placeholder(R.drawable.ic_launcher_background)
            }
            binding.progress.visibility = View.GONE
        }.addOnFailureListener {
            Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            binding.progress.visibility = View.GONE
        }
    }
}