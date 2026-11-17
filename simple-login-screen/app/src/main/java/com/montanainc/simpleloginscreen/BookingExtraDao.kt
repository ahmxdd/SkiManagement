package com.montanainc.simpleloginscreen

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.montanainc.simpleloginscreen.entities.BookingExtra
import com.montanainc.simpleloginscreen.entities.Equipment
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingExtraDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveExtra(extra: BookingExtra)

    @Query("UPDATE booking_extra SET late_fee = :fee WHERE booking_id = :bookingId")
    suspend fun setLateFee(bookingId: String, fee: Double): Int

    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    fun equipmentById(id: Int): Flow<Equipment?>

    @Query("""
        SELECT r.id AS id,
               r.booking_id AS booking_id,
               r.status AS status,
               r.equipment_id AS equipment_id,
               e.name AS item_name,
               e.price_per_day AS price_per_day,
               r.start_date AS start_date,
               r.end_date AS end_date,
               COALESCE(x.total_cost, MAX(1, ROUND((r.end_date - r.start_date) / 86400000.0)) * e.price_per_day) AS total_cost,
               COALESCE(x.late_fee, 0.0) AS late_fee,
               r.special_request AS request,
               r.equipment_info AS equipment,
               COALESCE(u.first_name || ' ' || u.last_name, 'Unknown') AS customer
        FROM rental r
        JOIN equipment e ON r.equipment_id = e.id
        LEFT JOIN users u ON r.user_id = u.uid
        LEFT JOIN booking_extra x ON x.booking_id = r.booking_id
        ORDER BY r.start_date DESC
    """)
    fun getSchedule(): Flow<List<RentalSchedule>>

    // Money counts once the gear has been picked up (customers pay in person). Cancelled bookings are excluded.
    @Query("""
        SELECT r.start_date AS start_date,
               COALESCE(x.total_cost, MAX(1, ROUND((r.end_date - r.start_date) / 86400000.0)) * e.price_per_day)
                 + COALESCE(x.late_fee, 0.0) AS amount
        FROM rental r
        JOIN equipment e ON r.equipment_id = e.id
        LEFT JOIN booking_extra x ON x.booking_id = r.booking_id
        WHERE r.status IN ('CHECKED_OUT', 'OVERDUE', 'RETURNED')
    """)
    fun getRevenueRows(): Flow<List<RevenueRow>>

    @Query("SELECT COUNT(*) FROM rental WHERE booking_id LIKE 'HIST%' OR booking_id LIKE 'DEMO%'")
    suspend fun countDemo(): Int
}

data class RentalSchedule(
    val id: Int,
    @ColumnInfo(name = "booking_id") val bookingId: String,
    val status: String,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "item_name") val itemName: String,
    @ColumnInfo(name = "price_per_day") val pricePerDay: Double,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long,
    @ColumnInfo(name = "total_cost") val totalCost: Double,
    @ColumnInfo(name = "late_fee") val lateFee: Double,
    val customer: String,
    val equipment: String,
    val request: String
)

data class RevenueRow(
    @ColumnInfo(name = "start_date") val startDate: Long,
    val amount: Double
)
