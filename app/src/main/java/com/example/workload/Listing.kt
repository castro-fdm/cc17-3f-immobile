package com.example.workload

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listings")
data class Listing(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val imageUri: String,
    val title: String,
    val description: String,
    val price: String,
    val isAccepted: Boolean = false,
    val isCompleted: Boolean = false
)



