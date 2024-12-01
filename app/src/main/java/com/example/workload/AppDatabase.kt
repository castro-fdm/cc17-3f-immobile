package com.example.workload

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Listing::class, User::class], version = 2, exportSchema = false)  // Updated version to 2
abstract class AppDatabase : RoomDatabase() {

    abstract fun listingDao(): ListingDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Define migration strategy
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add new columns 'role' and 'phoneNumber' to the 'user_table'
                database.execSQL("ALTER TABLE user_table ADD COLUMN role TEXT")
                database.execSQL("ALTER TABLE user_table ADD COLUMN phoneNumber TEXT")
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
                    .addMigrations(MIGRATION_1_2)  // Add the migration strategy
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
