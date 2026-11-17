package com.montanainc.simpleloginscreen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.montanainc.simpleloginscreen.entities.Discount
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(discount: Discount)

    @Query("SELECT * FROM discount ORDER BY code")
    fun getAll(): Flow<List<Discount>>

    @Query("SELECT * FROM discount WHERE code = :code AND active = 1 LIMIT 1")
    suspend fun findActive(code: String): Discount?

    @Query("UPDATE discount SET active = :active WHERE code = :code")
    suspend fun setActive(code: String, active: Boolean)

    @Query("SELECT COUNT(*) FROM discount")
    suspend fun count(): Int
}
