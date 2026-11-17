package com.montanainc.simpleloginscreen.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "waitlist", indices = [Index("equipment_id")])
data class WaitlistEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    val email: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis(),
    val notified: Boolean = false
)