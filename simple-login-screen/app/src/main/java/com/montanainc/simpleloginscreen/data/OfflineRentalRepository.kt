package com.montanainc.simpleloginscreen.data

import com.montanainc.simpleloginscreen.RentalDao
import com.montanainc.simpleloginscreen.entities.Rental
import kotlinx.coroutines.flow.Flow

class OfflineRentalRepository (private val rentalDao: RentalDao) : RentalRepository {
    override suspend fun insertRental(rental: Rental) = rentalDao.insert(rental)

    override suspend fun setStatus(rentalId: Int, status: String)  = rentalDao.setStatus(rentalId, status)

    override fun getAll(): Flow<List<Rental>> = rentalDao.getAll()

    override fun getForUser(userId: Int): Flow<List<Rental>> = rentalDao.getForUser(userId)

}