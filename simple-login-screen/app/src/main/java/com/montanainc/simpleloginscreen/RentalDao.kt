package com.montanainc.simpleloginscreen

import androidx.room.ColumnInfo
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

    @Query("""
        SELECT r.id AS id, 
               r.booking_id AS booking_id, 
               r.status AS status,
               r.equipment_id AS equipment_id, 
               e.name AS item_name,
               r.special_request AS request,
               r.equipment_info AS equipment,
               COALESCE(u.first_name || ' ' || u.last_name, 'Unknown') AS customer

        FROM rental r
        JOIN equipment e ON r.equipment_id = e.id
        LEFT JOIN users u ON r.user_id = u.uid
        ORDER BY r.start_date DESC
    """)
    suspend fun getAllDetailsForExport(): List<RentalDetails>

    @Query("SELECT COUNT(*) FROM rental")
    suspend fun count(): Int

    @Query("""
        SELECT r.id AS id, 
               r.booking_id AS booking_id, 
               r.status AS status,
               r.equipment_id AS equipment_id, 
               e.name AS item_name,
               r.special_request AS request,
               r.equipment_info AS equipment,
               COALESCE(u.first_name || ' ' || u.last_name, 'Unknown') AS customer

        FROM rental r
        JOIN equipment e ON r.equipment_id = e.id
        LEFT JOIN users u ON r.user_id = u.uid
        ORDER BY r.start_date DESC
    """)
    fun getAllWithDetails(): Flow<List<RentalDetails>>
}

data class RentalDetails(
    val id: Int,
    @ColumnInfo(name = "booking_id") val bookingId: String,
    val status: String,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "item_name") val itemName: String,
    val customer: String,
    val equipment: String,
    val request: String
)
