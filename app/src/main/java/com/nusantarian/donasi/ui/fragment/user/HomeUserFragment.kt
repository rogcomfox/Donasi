package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentHomeUserBinding

class HomeUserFragment : Fragment() {

    private var _binding: FragmentHomeUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_user_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_history ->
                findNavController().navigate(HomeUserFragmentDirections.actionHomeUserFragmentToHistoryUserFragment())
            R.id.nav_profile ->
                findNavController().navigate(HomeUserFragmentDirections.actionHomeUserFragmentToProfileFragment())
        }
        return super.onOptionsItemSelected(item)
    }
}