package com.montanainc.simpleloginscreen.data

import com.montanainc.simpleloginscreen.RentalDetails
import com.montanainc.simpleloginscreen.entities.Rental
import com.montanainc.simpleloginscreen.entities.User
import kotlinx.coroutines.flow.Flow

interface RentalRepository {

    suspend fun insertRental(rental: Rental)
    suspend fun setStatus(rentalId: Int, status: String)

    fun getAll(): Flow<List<Rental>>

    fun getForUser(userId: Int): Flow<List<Rental>>

    suspend fun getRentalExportDetails (): List<RentalDetails>
}