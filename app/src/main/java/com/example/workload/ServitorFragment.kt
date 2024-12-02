package com.example.workload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ServitorFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var listingDao: ListingDao
    private lateinit var adapter: ServitorListingsAdapter
    private val listings = mutableListOf<Listing>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_servitor, container, false)

        // Initialize Room Database
        db = AppDatabase.getDatabase(requireContext())
        listingDao = db.listingDao()

        // RecyclerView setup
        val recyclerView: RecyclerView = layout.findViewById(R.id.recyclerView)
        adapter = ServitorListingsAdapter(
            listings,
            onAccept = { listing -> acceptJob(listing) },
            onReject = { listing -> rejectJob(listing) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Button functionality
        val availableJobsButton: Button = layout.findViewById(R.id.availableJobsButton)
        val acceptedJobsButton: Button = layout.findViewById(R.id.acceptedJobsButton)

        availableJobsButton.setOnClickListener { loadAvailableJobs() }
        acceptedJobsButton.setOnClickListener { loadAcceptedJobs() }

        // Load initial job listings
        loadAvailableJobs()

        return layout
    }

    // Load available (non-accepted) jobs
    private fun loadAvailableJobs() {
        lifecycleScope.launch {
            val dbListings = listingDao.getAvailableJobs() // Fetch only non-accepted jobs
            listings.clear()
            listings.addAll(dbListings)
            adapter.notifyDataSetChanged()
        }
    }

    // Load accepted jobs
    private fun loadAcceptedJobs() {
        lifecycleScope.launch {
            val dbListings = listingDao.getAcceptedJobs() // Fetch only accepted jobs
            listings.clear()
            listings.addAll(dbListings)
            adapter.notifyDataSetChanged()
        }
    }

    // Mark a job as accepted
    private fun acceptJob(listing: Listing) {
        lifecycleScope.launch {
            val updatedListing = listing.copy(isAccepted = true)
            listingDao.updateListing(updatedListing)
            loadAvailableJobs() // Refresh to show only non-accepted jobs
        }
    }

    // Mark a job as rejected or perform other rejection logic
    private fun rejectJob(listing: Listing) {
        lifecycleScope.launch {
            listingDao.deleteListing(listing) // Or update with a "rejected" status
            loadAvailableJobs() // Refresh the list
        }
    }
}
