package com.montanainc.simpleloginscreen.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banner")
data class AppBanner(
    @PrimaryKey val id: Int = 1,
    val message: String,
    @ColumnInfo(name = "is_active") val isActive: Boolean
)
