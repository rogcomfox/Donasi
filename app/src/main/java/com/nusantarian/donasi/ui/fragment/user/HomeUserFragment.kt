package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentHomeUserBinding
import com.nusantarian.donasi.model.Donation
import com.nusantarian.donasi.model.HomeDonation
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlin.math.log

class HomeUserFragment : Fragment() {

    private var _binding: FragmentHomeUserBinding? = null
    private val binding get() = _binding!!
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        linearLayoutManager = LinearLayoutManager(activity)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.rv_donations).layoutManager = linearLayoutManager



        Log.d("HomeUser", "Display Donations")
        displayDonations(view)
    }

    private fun displayDonations(view: View) {
        val db = FirebaseFirestore.getInstance()
        val adapter = GroupAdapter<GroupieViewHolder>()
        val donationList = mutableListOf<HomeDonation>()

        db.collection("donations")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var donation: Donation = Donation.docToDonation(document)
                    Log.d("HomeUser", document.id)
                    //untuk search nanti modif ini
                    donationList.add(HomeDonation(donation, document.id))
                }

                donationList.reversed().forEach() {
                    adapter.add(it)
                    Log.d("HomeUser", it.donation.title)

                }
            }

        view.findViewById<RecyclerView>(R.id.rv_donations).adapter = adapter
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