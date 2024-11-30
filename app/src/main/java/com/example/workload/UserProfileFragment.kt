package com.example.workload

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao
    private var selectedImageUri: String? = null

    private lateinit var profileImageView: ImageView
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var roleRadioGroup: RadioGroup
    private lateinit var saveButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Room Database
        db = AppDatabase.getDatabase(requireContext())
        userDao = db.userDao()

        // Find Views
        profileImageView = view.findViewById(R.id.profileImageView)
        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        phoneEditText = view.findViewById(R.id.phoneEditText)
        roleRadioGroup = view.findViewById(R.id.roleRadioGroup)
        saveButton = view.findViewById(R.id.saveProfileButton)

        // Load user data
        loadUserProfile()

        // Handle profile image selection
        profileImageView.setOnClickListener { openGalleryForImage() }

        // Save button click listener
        saveButton.setOnClickListener {
            saveUserProfile()
        }
    }

    // Load user profile data
    private fun loadUserProfile() {
        lifecycleScope.launch {
            val user = userDao.getUser()
            user?.let {
                // Populate the fields with existing user data
                nameEditText.setText(it.name)
                emailEditText.setText(it.email)
                phoneEditText.setText(it.phoneNumber)
                selectedImageUri = it.profilePictureUri

                // Set role
                val roleRadioButtonId = if (it.role == "Servitor") R.id.servitorRadioButton else R.id.requesteeRadioButton
                roleRadioGroup.check(roleRadioButtonId)

                // Load profile picture if available
                selectedImageUri?.let { uri ->
                    Glide.with(requireContext()).load(uri).into(profileImageView)
                }
            }
        }
    }

    // Save user profile data
    private fun saveUserProfile() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val phoneNumber = phoneEditText.text.toString()
        val roleId = roleRadioGroup.checkedRadioButtonId
        val role = view?.findViewById<RadioButton>(roleId)?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty() && phoneNumber.isNotEmpty()) {
            val user = User(
                id = 1, // Assuming a single user, otherwise use a dynamic ID
                name = name,
                email = email,
                profilePictureUri = selectedImageUri ?: "",
                role = role,
                phoneNumber = phoneNumber
            )
            lifecycleScope.launch {
                userDao.updateUser(user)
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
        }
    }

    // Open gallery to select a profile picture
    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        resultLauncher.launch(intent)
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedImageUri = uri.toString()
                    Glide.with(requireContext()).load(uri).into(profileImageView)
                }
            }
        }
}
