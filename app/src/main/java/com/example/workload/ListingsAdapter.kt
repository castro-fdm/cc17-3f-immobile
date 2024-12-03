package com.example.workload

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ListingsAdapter(
    private val listings: MutableList<Listing>,
    private val onEdit: (Listing) -> Unit,
    private val onDelete: (Listing) -> Unit
) : RecyclerView.Adapter<ListingsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.listingImage)
        val titleView: TextView = itemView.findViewById(R.id.listingTitle)
        val descriptionView: TextView = itemView.findViewById(R.id.listingDescription)
        val priceView: TextView = itemView.findViewById(R.id.listingPrice)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)

        fun bind(listing: Listing) {
            imageView.setImageURI(Uri.parse(listing.imageUri))
            titleView.text = listing.title
            descriptionView.text = listing.description
            priceView.text = listing.price

            // Hide the Edit button if the listing is completed
            if (listing.isCompleted) {
                editButton.visibility = View.GONE
            } else {
                editButton.visibility = View.VISIBLE
            }

            editButton.setOnClickListener { onEdit(listing) }
            deleteButton.setOnClickListener { onDelete(listing) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.listing_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listings[position])
    }

    override fun getItemCount() = listings.size
}

