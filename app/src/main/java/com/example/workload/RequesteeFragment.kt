package com.example.workload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RequesteeFragment : Fragment() {

    private val PICK_IMAGE_REQUEST_CODE = 1
    private var selectedImageUri: Uri? = null
    private lateinit var adapter: ListingsAdapter
    private val listings = mutableListOf<Listing>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_requestee, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val addButton: ImageButton = view.findViewById(R.id.imageButton)

        adapter = ListingsAdapter(
            listings,
            onEdit = { listing -> showAddEditDialog(listing) },
            onDelete = { listing ->
                listings.remove(listing)
                adapter.notifyDataSetChanged()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        addButton.setOnClickListener {
            showAddEditDialog(null)
        }

        return view
    }

    private fun showAddEditDialog(existingListing: Listing?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_edit_listing, null)

        val imageInput: ImageView = dialogView.findViewById(R.id.imageInput)
        val titleInput: EditText = dialogView.findViewById(R.id.titleInput)
        val descriptionInput: EditText = dialogView.findViewById(R.id.descriptionInput)
        val priceInput: EditText = dialogView.findViewById(R.id.priceInput)

        existingListing?.let {
            titleInput.setText(it.title)
            descriptionInput.setText(it.description)
            priceInput.setText(it.price)
            selectedImageUri = Uri.parse(it.imageUri)
            imageInput.setImageURI(selectedImageUri)
        }

        imageInput.setOnClickListener {
            openGallery()
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val listing = Listing(
                    imageUri = selectedImageUri?.toString() ?: "",
                    title = titleInput.text.toString(),
                    description = descriptionInput.text.toString(),
                    price = priceInput.text.toString()
                )

                if (existingListing != null) {
                    val index = listings.indexOf(existingListing)
                    listings[index] = listing
                    adapter.notifyItemChanged(index)
                } else {
                    listings.add(listing)
                    adapter.notifyItemInserted(listings.size - 1)
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            if (selectedImageUri != null) {
                // Update the ImageView in the dialog with the selected image
                val dialogImageView: ImageView? = view?.findViewById(R.id.imageInput)
                dialogImageView?.setImageURI(selectedImageUri)
            } else {
                Toast.makeText(requireContext(), "Failed to select image", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
