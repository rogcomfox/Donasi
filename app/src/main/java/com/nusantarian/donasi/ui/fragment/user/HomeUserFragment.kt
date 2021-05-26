package com.nusantarian.donasi.ui.fragment.user

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentHomeUserBinding
import com.nusantarian.donasi.model.Donation
import com.nusantarian.donasi.model.HomeDonation
import com.nusantarian.donasi.ui.activity.LandingActivity
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder

class HomeUserFragment : Fragment() {

    private var _binding: FragmentHomeUserBinding? = null
    private val binding get() = _binding!!
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserData()
        setSearchView()
        loadDonationData(arrayOf(""))
    }

    private fun loadUserData() {
        val userDoc = db.collection("users").document(auth.currentUser!!.uid)
        userDoc.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.tvIntro.text = "Hello, " + document["name"]
                } else {
                    binding.tvIntro.text = "Hello, NULL"
                }
            }
    }

    private fun setSearchView() {
        val searchView = binding.svHome

        searchView.setIconified(false)
        searchView.clearFocus()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val queries = query.lowercase().split(" ").toTypedArray()
                    loadDonationData(queries)
                    binding.tvCategory1.text =
                        "Results for " + '"' + queries[0] + "..." + '"'
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        searchView.setOnCloseListener(object : SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                loadDonationData(arrayOf(""))
                binding.tvCategory1.text =
                    resources.getString(R.string.tv_category_1)
                return false
            }

        })
    }

    private fun loadDonationData(keyword: Array<String>) {
        binding.rvDonations.layoutManager = GridLayoutManager(requireContext(), 2)

        val adapter = GroupAdapter<GroupieViewHolder>()
        val donationList = mutableListOf<HomeDonation>()

        db.collection("donations")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val donation: Donation = Donation.docToDonation(document)
                    val homeDonation = HomeDonation(donation, document.id, this)


                    //Search System
                    if (donation.currentDuration() >= 0) {
                        if (keyword[0] == "") {
                            donationList.add(homeDonation)
                        } else {
                            var point = 0
                            keyword.forEach {
                                if (donation.keywords().contains(it)) {
                                    point++
                                }
                            }
                            if (point == keyword.size) {
                                donationList.add(homeDonation)
                            }
                        }
                    }
                }

                //Sorting Latest Donation
                donationList.reversed().forEach {
                    adapter.add(it)
                }

                adapter.setOnItemClickListener { item, view ->
                    val donation = item as HomeDonation

                    findNavController()
                        .navigate(
                            HomeUserFragmentDirections.actionHomeUserFragmentToDetailDonationFragment(
                                donation.key
                            )
                        )
                }

                binding.rvDonations.adapter = adapter
            }

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
            R.id.nav_log_out -> {
                auth.signOut()
                startActivity(Intent(requireActivity(), LandingActivity::class.java))
                requireActivity().finishAffinity()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}