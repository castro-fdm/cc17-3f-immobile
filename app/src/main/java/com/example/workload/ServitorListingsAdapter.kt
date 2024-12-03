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
    private val onReject: (Listing) -> Unit,
    private val onConfirm: (Listing) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_NORMAL = 1
        private const val VIEW_TYPE_CONFIRM = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (listings[position].isAccepted) VIEW_TYPE_CONFIRM else VIEW_TYPE_NORMAL
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_NORMAL) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.listing_item_servitor, parent, false)
            NormalViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.listing_item_servitor_confirm, parent, false)
            ConfirmViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val listing = listings[position]

        if (holder is NormalViewHolder) {
            holder.titleTextView.text = listing.title
            holder.descriptionTextView.text = listing.description
            holder.priceTextView.text = listing.price
            holder.imageView.setImageURI(Uri.parse(listing.imageUri))

            holder.acceptButton.setOnClickListener { onAccept(listing) }
            holder.rejectButton.setOnClickListener { onReject(listing) }
        } else if (holder is ConfirmViewHolder) {
            holder.titleTextView.text = listing.title
            holder.descriptionTextView.text = listing.description
            holder.priceTextView.text = listing.price
            holder.imageView.setImageURI(Uri.parse(listing.imageUri))

            holder.confirmButton.setOnClickListener { onConfirm(listing) }
        }
    }

    override fun getItemCount(): Int = listings.size

    inner class NormalViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.listingImage)
        val titleTextView: TextView = view.findViewById(R.id.listingTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.listingDescription)
        val priceTextView: TextView = view.findViewById(R.id.listingPrice)
        val acceptButton: Button = view.findViewById(R.id.acceptButton)
        val rejectButton: Button = view.findViewById(R.id.rejectButton)
    }

    inner class ConfirmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.listingImage)
        val titleTextView: TextView = view.findViewById(R.id.listingTitle)
        val descriptionTextView: TextView = view.findViewById(R.id.listingDescription)
        val priceTextView: TextView = view.findViewById(R.id.listingPrice)
        val confirmButton: Button = view.findViewById(R.id.confirmButton)
    }
}