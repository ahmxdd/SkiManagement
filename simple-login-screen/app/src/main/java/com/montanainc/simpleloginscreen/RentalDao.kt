package com.montanainc.simpleloginscreen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.montanainc.simpleloginscreen.entities.Rental
import kotlinx.coroutines.flow.Flow

@Dao
interface RentalDao {
    @Insert
    suspend fun insert(rental: Rental)

    @Query("SELECT * FROM rental WHERE user_id = :userId ORDER BY start_date DESC")
    fun getForUser(userId: Int): Flow<List<Rental>>

    @Query("SELECT * FROM rental")
    fun getAll(): Flow<List<Rental>>

    @Query("UPDATE rental SET status = :status WHERE id = :rentalId")
    suspend fun setStatus(rentalId: Int, status: String)
}