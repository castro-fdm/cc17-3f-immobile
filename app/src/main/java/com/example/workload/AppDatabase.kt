package com.example.workload

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Listing::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun listingDao(): ListingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "listings_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
