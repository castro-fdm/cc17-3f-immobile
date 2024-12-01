package com.example.workload

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServitorListingsAdapter(
    private val listings: List<Listing>,
    private val onAccept: (Listing) -> Unit,
    private val onReject: (Listing) -> Unit
) : RecyclerView.Adapter<ServitorListingsAdapter.ServitorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServitorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_item_servitor, parent, false)
        return ServitorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServitorViewHolder, position: Int) {
        val listing = listings[position]

        // Set the data for the listing item
        holder.titleTextView.text = listing.title
        holder.descriptionTextView.text = listing.description
        holder.priceTextView.text = listing.price
        holder.imageView.setImageURI(Uri.parse(listing.imageUri))

        // Button actions
        holder.acceptButton.setOnClickListener { onAccept(listing) }
        holder.rejectButton.setOnClickListener { onReject(listing) }
    }

    override fun getItemCount(): Int = listings.size

    inner class ServitorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.listingImage)
        val titleTextView: TextView = view.findViewById(R.id.listingTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.listingDescription)
        val priceTextView: TextView = view.findViewById(R.id.listingPrice)
        val acceptButton: Button = view.findViewById(R.id.acceptButton)
        val rejectButton: Button = view.findViewById(R.id.rejectButton)
    }
}
