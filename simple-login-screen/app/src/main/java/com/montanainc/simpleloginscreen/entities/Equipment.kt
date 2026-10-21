package com.montanainc.simpleloginscreen.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipment")
data class Equipment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,                 // SKI, SNOWBOARD, BOOTS, HELMET, GOGGLES
    val size: String,
    val brand: String,
    @ColumnInfo(name = "skill_level") val skillLevel: String,  // BEGINNER, INTERMEDIATE, ADVANCED
    @ColumnInfo(name = "price_per_day") val pricePerDay: Double,
    val status: String = "AVAILABLE", // AVAILABLE, RENTED
    @ColumnInfo(name = "is_broken") val isBroken: Boolean = false
)