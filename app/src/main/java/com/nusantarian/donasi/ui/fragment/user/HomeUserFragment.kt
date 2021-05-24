package com.nusantarian.donasi.ui.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nusantarian.donasi.R
import com.nusantarian.donasi.databinding.FragmentHomeUserBinding
import com.nusantarian.donasi.model.Donation
import com.nusantarian.donasi.model.HomeDonation
import com.nusantarian.donasi.ui.fragment.landing.LoginFragmentDirections
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlin.math.log

class HomeUserFragment : Fragment() {

    private var _binding: FragmentHomeUserBinding? = null
    private val binding get() = _binding!!
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeUserBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setIntroduction(view)
        setSearchView(view)
        displayDonations(view, arrayOf(""))
    }

    private fun setIntroduction(view: View) {
        val userDoc = db.collection("users").document(auth.currentUser.uid)
        userDoc.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    view.findViewById<TextView>(R.id.tv_intro).text = "Hello, " + document["name"]
                } else {
                    view.findViewById<TextView>(R.id.tv_intro).text = "Hello, NULL"
                }
            }
    }

    private fun setSearchView(view: View) {
        var searchView = view.findViewById<SearchView>(R.id.sv_home)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    val queries = query.split(" ").toTypedArray()
                    displayDonations(view, queries)
                    view.findViewById<TextView>(R.id.tv_category_1).text =
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
                displayDonations(view, arrayOf(""))
                view.findViewById<TextView>(R.id.tv_category_1).text =
                    resources.getString(R.string.tv_category_1)
                return false
            }

        })
    }

    private fun displayDonations(view: View, keyword: Array<String>) {
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        view.findViewById<RecyclerView>(R.id.rv_donations).layoutManager = linearLayoutManager

        val adapter = GroupAdapter<GroupieViewHolder>()
        val donationList = mutableListOf<HomeDonation>()

        db.collection("donations")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var donation: Donation = Donation.docToDonation(document)

                    //Search System
                    if (donation.currentDuration() >= 0) {
                        if (keyword[0].equals("")) {
                            donationList.add(HomeDonation(donation, document.id))
                        } else {
                            var point = 0
                            keyword.forEach {
                                if (donation.keywords().contains(it)) {
                                    point++
                                }
                            }
                            if (point == keyword.size) {
                                donationList.add(HomeDonation(donation, document.id))
                            }
                        }
                    }
                }

                //Sorting Latest Donation
                donationList.reversed().forEach() {
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