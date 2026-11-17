package com.montanainc.simpleloginscreen.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_extra")
data class BookingExtra(
    @PrimaryKey @ColumnInfo(name = "booking_id") val bookingId: String,
    @ColumnInfo(name = "total_cost") val totalCost: Double,
    @ColumnInfo(name = "late_fee") val lateFee: Double = 0.0
)
