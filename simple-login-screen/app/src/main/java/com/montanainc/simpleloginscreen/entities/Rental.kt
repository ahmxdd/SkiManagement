package com.montanainc.simpleloginscreen.entities

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "rental")
data class Rental(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "booking_id") val bookingId: String,
    @ColumnInfo(name = "user_id") val userId: Int,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "start_date") val startDate: Long,
    @ColumnInfo(name = "end_date") val endDate: Long,
    val status: String = "PENDING"    // PENDING, CONFIRMED, CANCELLED, RETURNED
)