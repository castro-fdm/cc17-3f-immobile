package com.example.workload

import androidx.room.*
import kotlinx.coroutines.Deferred

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings")
    suspend fun getAllListings(): List<Listing>

    @Query("SELECT * FROM listings WHERE isCompleted = 1")
    suspend fun getCompletedListings(): List<Listing>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: Listing)

    @Update
    suspend fun updateListing(listing: Listing)

    @Delete
    suspend fun deleteListing(listing: Listing)
    @Query("SELECT * FROM listings WHERE isAccepted = 1")
    suspend fun getAcceptedListings(): List<Listing>

    @Query("SELECT * FROM listings WHERE isAccepted = 0")
    suspend fun getRejectedListings(): List<Listing>
}

