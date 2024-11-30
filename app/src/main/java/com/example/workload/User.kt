package com.example.workload

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val profilePictureUri: String, // URI of the profile picture
    val role: String, // "servitor" or "requestee"
    val phoneNumber: String // User's phone number
)

