package com.montanainc.simpleloginscreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.montanainc.simpleloginscreen.data.RentalRepository
import com.montanainc.simpleloginscreen.entities.Rental
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RentalViewModel(private val rentalRepo: RentalRepository) : ViewModel() {
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
                status = "CONFIRMED"
            )
            rentalRepo.insertRental(rental = rental)
            _uiState.update { it.copy(bookingId = generatedBookingId) }
            sendAutomatedEmail(userEmail, generatedBookingId)
            onSuccess()
        }
    }


    fun cancelRental(rentalId: Int) {
        viewModelScope.launch {
            rentalRepo.setStatus(rentalId, "CANCELLED")
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