package com.example.workload

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Listing::class, User::class], version = 3, exportSchema = false)  // Updated version to 3
abstract class AppDatabase : RoomDatabase() {

    abstract fun listingDao(): ListingDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration for version 1 -> 2
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add 'role' and 'phoneNumber' columns to 'user_table'
                database.execSQL("ALTER TABLE user_table ADD COLUMN role TEXT")
                database.execSQL("ALTER TABLE user_table ADD COLUMN phoneNumber TEXT")
            }
        }

        // Migration for version 2 -> 3 (Add 'isAccepted' column)
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add 'isAccepted' column to 'listings' table
                database.execSQL("ALTER TABLE listings ADD COLUMN isAccepted INTEGER DEFAULT 0 NOT NULL")
            }
        }

        // Singleton pattern to ensure only one instance of the database is used
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "listings_database"  // Name of the database
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)  // Add migration strategies
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
