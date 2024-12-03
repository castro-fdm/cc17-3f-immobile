package com.example.workload

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException

class RequesteeFragment : Fragment() {

    private lateinit var db: AppDatabase
    private lateinit var listingDao: ListingDao
    private lateinit var adapter: ListingsAdapter
    private val listings = mutableListOf<Listing>()
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_requestee, container, false)

        val profileText = layout.findViewById<TextView>(R.id.profileText)
        profileText.setOnClickListener {
            val action = RequesteeFragmentDirections.actionRequesteeFragmentToUserProfileFragment()
            view?.findNavController()?.navigate(action)
        }

        // Initialize Room Database
        db = AppDatabase.getDatabase(requireContext())
        listingDao = db.listingDao()

        // RecyclerView setup
        val recyclerView: RecyclerView = layout.findViewById(R.id.recyclerView)
        adapter = ListingsAdapter(
            listings,
            onEdit = { listing -> showAddEditDialog(listing) },
            onDelete = { listing -> deleteListing(listing) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Add optional spacing between RecyclerView items
        recyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        // Load all listings initially
        loadListings()

        // Buttons
        val addButton: ImageButton = layout.findViewById(R.id.imageButton)
        val yourListingsButton: Button = layout.findViewById(R.id.yourListingsButton)
        val completedListingsButton: Button = layout.findViewById(R.id.completedListingsButton)

        // Button listeners
        addButton.setOnClickListener { showAddEditDialog(null) }
        yourListingsButton.setOnClickListener { loadListings() } // Load all active listings (non-completed)
        completedListingsButton.setOnClickListener { loadCompletedListings() } // Load completed listings

        return layout
    }

    // Load all listings except the completed ones
    private fun loadListings() {
        lifecycleScope.launch {
            val dbListings = listingDao.getAllListings().filter { !it.isCompleted } // Exclude completed jobs
            listings.clear()
            listings.addAll(dbListings)
            adapter.notifyDataSetChanged()
        }
    }

    // Load only completed listings
    private fun loadCompletedListings() {
        lifecycleScope.launch {
            val dbListings = listingDao.getCompletedListings() // Fetch only completed jobs
            listings.clear()
            listings.addAll(dbListings)
            adapter.notifyDataSetChanged()
        }
    }

    // Show Add/Edit dialog
    private fun showAddEditDialog(existingListing: Listing?) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_edit_listing, null)

        val imageInput: ImageView = dialogView.findViewById(R.id.imageInput)
        val titleInput: EditText = dialogView.findViewById(R.id.titleInput)
        val descriptionInput: EditText = dialogView.findViewById(R.id.descriptionInput)
        val priceInput: EditText = dialogView.findViewById(R.id.priceInput)

        // Populate fields if editing an existing listing
        existingListing?.let {
            titleInput.setText(it.title)
            descriptionInput.setText(it.description)
            priceInput.setText(it.price)

            // Carefully handle existing image URI
            if (it.imageUri.isNotEmpty()) {
                try {
                    // Convert file path to Uri
                    selectedImageUri = Uri.fromFile(File(it.imageUri))
                    imageInput.setImageURI(selectedImageUri)
                } catch (e: Exception) {
                    Log.e("ImageError", "Error loading existing image", e)
                    Toast.makeText(requireContext(), "Could not load existing image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        imageInput.setOnClickListener { openGallery() }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val imageUriToSave = if (selectedImageUri != null) {
                    // Only copy URI if a new image was selected
                    copyUriToInternalStorage(selectedImageUri!!)
                } else {
                    // Use existing image path if no new image was selected
                    existingListing?.imageUri ?: ""
                }

                val newListing = Listing(
                    id = existingListing?.id ?: 0,
                    imageUri = imageUriToSave,
                    title = titleInput.text.toString(),
                    description = descriptionInput.text.toString(),
                    price = priceInput.text.toString()
                )

                lifecycleScope.launch {
                    if (existingListing != null) {
                        listingDao.updateListing(newListing)
                    } else {
                        listingDao.insertListing(newListing)
                    }
                    loadListings()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    // Delete a listing
    private fun deleteListing(listing: Listing) {
        lifecycleScope.launch {
            listingDao.deleteListing(listing)
            loadListings()
        }
    }

    // Open gallery for image selection
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMISSION
            )
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_STORAGE_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                view?.findViewById<ImageView>(R.id.imageInput)?.setImageURI(uri)
                Log.d("RequesteeFragment", "Selected Image URI: $uri")
            } ?: Toast.makeText(requireContext(), "Failed to select image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyUriToInternalStorage(uri: Uri): String {
        try {
            // Create the images directory if it doesn't exist
            val imagesDir = File(requireContext().filesDir, "images")
            if (!imagesDir.exists()) {
                imagesDir.mkdirs() // Create the directory
            }

            // Check if input stream can be opened
            val inputStream = requireContext().contentResolver.openInputStream(uri)
                ?: throw IllegalArgumentException("Unable to open input stream for URI: $uri")

            // Now create the image file inside this directory
            val file = File(imagesDir, "${System.currentTimeMillis()}.jpg")

            // Use use to ensure streams are closed
            inputStream.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Verify file was actually created
            if (!file.exists()) {
                throw IOException("File was not created successfully")
            }

            return file.absolutePath // Return the file path to save in your database
        } catch (e: Exception) {
            Log.e("FileError", "Error copying file to internal storage", e)
            throw e // Or handle as appropriate for your app's error handling strategy
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 100
        private const val REQUEST_STORAGE_PERMISSION = 101
    }
}
