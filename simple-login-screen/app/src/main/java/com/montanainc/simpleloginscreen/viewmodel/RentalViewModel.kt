package com.montanainc.simpleloginscreen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.montanainc.simpleloginscreen.data.EquipmentRepository
import com.montanainc.simpleloginscreen.data.RentalRepository
import com.montanainc.simpleloginscreen.entities.Rental
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Only bookings that haven't been picked up yet can be cancelled
val CANCELLABLE_STATUSES = setOf("CONFIRMED", "RESERVED")

class RentalViewModel(private val rentalRepo: RentalRepository, private val equipmentDao: EquipmentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RentalUiState())
    val uiState: StateFlow<RentalUiState> = _uiState.asStateFlow()

    fun registerRental(onSuccess: () -> Unit, userId: Int, equipmentId: Int, userEmail: String) {
        viewModelScope.launch {
            val currentState = uiState.value
            val daysInt = currentState.days.toIntOrNull() ?: 1
            val generatedBookingId = java.util.UUID.randomUUID().toString().take(8).uppercase()
            val rental = Rental(
                bookingId = generatedBookingId,
                userId = userId,
                equipmentId = equipmentId,
                startDate = System.currentTimeMillis(),
                endDate = System.currentTimeMillis() + (daysInt * 86400000L),
                status = "RESERVED",
                specialRequest = currentState.specialRequests,
                equipmentInfo = currentState.equipment
            )
            try {
                rentalRepo.insertRental(rental = rental)
                equipmentDao.setStatus(equipmentId, "RESERVED")
                _uiState.update { it.copy(bookingId = generatedBookingId) }

                sendAutomatedEmail(userEmail, generatedBookingId)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    // Cancels a booking that hasn't been picked up yet and puts the equipment back on the shelf
    fun cancelRental(rental: Rental) {
        if (rental.status !in CANCELLABLE_STATUSES) return
        viewModelScope.launch {
            try {
                rentalRepo.setStatus(rental.id, "CANCELLED")
                equipmentDao.setStatus(rental.equipmentId, "AVAILABLE")
            } catch (e: Exception) {
                Log.e("RentalViewModel", "Failed to cancel rental ${rental.id}", e)
            }
        }
    }

    fun updateDays(days: String) {
        _uiState.update { currentState ->
            currentState.copy(days = days)
        }
    }

    fun updateWaiver(signed: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(waverSigned = signed)
        }
    }
    fun updateEquipment(equipment: String) {
        _uiState.update { currentState ->
            currentState.copy(equipment = equipment)
        }
    }

    fun updateSpecialRequests(request: String) {
        _uiState.update { currentState ->
            currentState.copy(specialRequests = request)
        }
    }

    fun updateAge(age: String) {
        _uiState.update { currentState ->
            currentState.copy(age = age)
        }
    }

    fun updateHeight(height: String) {
        _uiState.update { currentState ->
            currentState.copy(heightCm = height)
        }

    }
    fun getRentalForUser(userId: Int): Flow<List<Rental>> {
        val rentals = rentalRepo.getForUser(userId)
        return rentals

        }
    fun exportCSV () {
        viewModelScope.launch {
            rentalRepo.getRentalExportDetails()
        }
    }
    private fun sendAutomatedEmail(userEmail: String, bookingId: String) {
        val emailPayLoad = hashMapOf(
            "to" to listOf(userEmail),
            "message" to hashMapOf(
                "subject" to "Rental Confirmation $bookingId",
                "text" to "Thank you for your purchase and choosing us! Your booking id is $bookingId"
            )
        )
        FirebaseFirestore.getInstance().collection("mail").add(emailPayLoad)
    }
    }