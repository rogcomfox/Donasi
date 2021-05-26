package com.nusantarian.donasi.ui.fragment.admin

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentMainAdminBinding
import com.nusantarian.donasi.ui.activity.LandingActivity

class MainAdminFragment : Fragment() {

    private var _binding: FragmentMainAdminBinding? = null
    private val binding get() = _binding!!
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainAdminBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAdd.setOnClickListener {
            findNavController()
                .navigate(MainAdminFragmentDirections.actionMainAdminFragmentToAddDonationFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_admin_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_receipt ->
                findNavController().navigate(MainAdminFragmentDirections.actionMainAdminFragmentToPaymentOrgFragment())
            R.id.nav_profile ->
                Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show()
            R.id.nav_log_out -> {
                auth.signOut()
                startActivity(Intent(requireActivity(), LandingActivity::class.java))
                requireActivity().finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}