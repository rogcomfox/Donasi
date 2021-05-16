package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nusantarian.donasi.databinding.FragmentHistoryUserBinding

class HistoryUserFragment : Fragment() {

    private var _binding: FragmentHistoryUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryUserBinding.inflate(inflater, container, false)
        return binding.root
    }
}