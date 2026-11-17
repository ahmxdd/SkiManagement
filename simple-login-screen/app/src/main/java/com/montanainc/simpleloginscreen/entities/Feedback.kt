package com.montanainc.simpleloginscreen.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "feedback", indices = [Index("equipment_id")])
data class Feedback(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "equipment_id") val equipmentId: Int,
    @ColumnInfo(name = "user_id") val userId: Int,
    val rating: Int,                 // 1 to 5
    val comment: String,
    @ColumnInfo(name = "created_at") val createdAt: Long = System.currentTimeMillis()
)
