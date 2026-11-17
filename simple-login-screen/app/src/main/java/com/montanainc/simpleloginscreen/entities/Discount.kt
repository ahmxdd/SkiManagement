package com.montanainc.simpleloginscreen.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discount")
data class Discount(
    @PrimaryKey val code: String,
    val amount: Double,
    val active: Boolean = true
)
