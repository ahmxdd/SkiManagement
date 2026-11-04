package com.montanainc.simpleloginscreen.screens

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class EquipmentDetails (
    val id: Int = 0,
    val name: String = "",
    val type: String = "",                 // SKI, SNOWBOARD, BOOTS, HELMET, GOGGLES
    val size: String = "",
    val brand: String = "",
    val skillLevel: String = "",  // BEGINNER, INTERMEDIATE, ADVANCED
    val pricePerDay: Double,
    val status: String = "AVAILABLE", // AVAILABLE, RENTED
    val isBroken: Boolean = false

)