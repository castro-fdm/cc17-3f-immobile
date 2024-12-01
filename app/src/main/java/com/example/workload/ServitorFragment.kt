package com.example.workload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            onAccept = { listing -> acceptListing(listing) },
            onReject = { listing -> rejectListing(listing) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Load all listings (or filtered by status if needed)
        loadListings()

        return layout
    }

    // Load listings from the database
    private fun loadListings() {
        lifecycleScope.launch {
            val dbListings = listingDao.getAllListings() // Adjust this query as necessary
            listings.clear()
            listings.addAll(dbListings)
            adapter.notifyDataSetChanged()
        }
    }

    // Accept a listing
    private fun acceptListing(listing: Listing) {
        lifecycleScope.launch {
            // Create a new instance with the updated status
            val updatedListing = listing.copy(isAccepted = true)
            listingDao.updateListing(updatedListing) // Update the listing in the database
            loadListings() // Refresh the list
        }
    }

    // Reject a listing
    private fun rejectListing(listing: Listing) {
        lifecycleScope.launch {
            // Create a new instance with the updated status
            val updatedListing = listing.copy(isAccepted = false)
            listingDao.updateListing(updatedListing) // Update the listing in the database
            loadListings() // Refresh the list
        }
    }


}
