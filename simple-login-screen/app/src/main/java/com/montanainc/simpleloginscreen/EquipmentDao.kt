package com.montanainc.simpleloginscreen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.montanainc.simpleloginscreen.entities.Equipment
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment WHERE is_broken = 0")
    fun getAllVisible(): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE status = 'AVAILABLE' AND is_broken = 0")
    fun getAvailable(): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE name LIKE '%' || :query || '%' AND is_broken = 0")
    fun search(query: String): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE skill_level = :level AND is_broken = 0")
    fun getBySkillLevel(level: String): Flow<List<Equipment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(equipment: Equipment)

    @Update
    suspend fun update(equipment: Equipment)

    @Query("UPDATE equipment SET status = :status WHERE id = :id")
    suspend fun setStatus(id: Int, status: String)

    @Query("SELECT * FROM equipment ORDER BY name")
    fun getAll(): Flow<List<Equipment>>

    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    fun getById(id: Int): Flow<Equipment?>

    @Query("SELECT COUNT(*) FROM equipment")
    suspend fun count(): Int
}