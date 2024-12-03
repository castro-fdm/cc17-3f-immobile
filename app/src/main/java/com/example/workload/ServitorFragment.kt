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
            onReject = { listing -> rejectJob(listing) },
            onMarkComplete = { listing -> markJobAsComplete(listing) } // Pass the lambda here
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
            loadAcceptedJobs() // Refresh to show the updated UI for accepted jobs
        }
    }

    // Mark a job as rejected or perform other rejection logic
    private fun rejectJob(listing: Listing) {
        lifecycleScope.launch {
            listingDao.deleteListing(listing) // Or update with a "rejected" status
            loadAvailableJobs() // Refresh the list
        }
    }

    // Mark the job as completed and remove it from the available list
    private fun markJobAsComplete(listing: Listing) {
        lifecycleScope.launch {
            // Mark the job as completed in the database
            val updatedListing = listing.copy(isCompleted = true)
            listingDao.updateListing(updatedListing)

            // Remove the completed job from the accepted listings and refresh the UI
            listings.remove(listing)
            adapter.notifyDataSetChanged()

            // Optionally, you can load the completed jobs in a separate list or RecyclerView in the RequesteeFragment
            loadAcceptedJobs() // Refresh to show updated list of accepted jobs
        }
    }

    // Confirm an accepted job (if necessary)
    private fun confirmJob(listing: Listing) {
        lifecycleScope.launch {
            val updatedListing = listing.copy(isCompleted = true) // Mark as completed
            listingDao.updateListing(updatedListing)
            loadAcceptedJobs() // Refresh the accepted jobs list
        }
    }
}
