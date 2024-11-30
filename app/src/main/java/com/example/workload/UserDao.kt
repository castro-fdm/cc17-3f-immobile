package com.example.workload

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    // Insert a new user
    @Insert
    suspend fun insertUser(user: User)

    // Get the current user (assuming a single user for simplicity)
    @Query("SELECT * FROM user_table WHERE id = 1")
    suspend fun getUser(): User?

    // Update user profile
    @Update
    suspend fun updateUser(user: User)
}
