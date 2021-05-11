package com.nusantarian.donasi.ui.fragment.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentHomeUserBinding
import com.nusantarian.donasi.databinding.FragmentRegisterBinding

class HomeUserFragment : Fragment() {

    private var _binding: FragmentHomeUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        return binding.root
    }


}