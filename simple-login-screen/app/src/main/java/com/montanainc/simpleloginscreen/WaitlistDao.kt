package com.montanainc.simpleloginscreen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.montanainc.simpleloginscreen.entities.WaitlistEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface WaitlistDao {
    @Insert
    suspend fun insert(entry: WaitlistEntry)

    @Query("SELECT * FROM waitlist WHERE notified = 0 ORDER BY created_at ASC")
    fun getWaiting(): Flow<List<WaitlistEntry>>

    @Query("SELECT * FROM waitlist WHERE equipment_id = :equipmentId AND notified = 0 ORDER BY created_at ASC LIMIT 1")
    suspend fun firstInQueue(equipmentId: Int): WaitlistEntry?

    @Query("SELECT COUNT(*) FROM waitlist WHERE equipment_id = :equipmentId AND user_id = :userId AND notified = 0")
    suspend fun alreadyQueued(equipmentId: Int, userId: Int): Int

    @Query("UPDATE waitlist SET notified = 1 WHERE id = :id")
    suspend fun markNotified(id: Int)
}
