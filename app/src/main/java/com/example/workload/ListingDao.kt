package com.example.workload

import androidx.room.*

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings")
    suspend fun getAllListings(): List<Listing>

    @Query("SELECT * FROM listings WHERE isCompleted = 1") // Assuming isCompleted column exists
    suspend fun getCompletedListings(): List<Listing>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: Listing)

    @Update
    suspend fun updateListing(listing: Listing)

    @Delete
    suspend fun deleteListing(listing: Listing)
}

