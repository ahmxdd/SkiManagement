package com.montanainc.simpleloginscreen.viewmodel

import com.montanainc.simpleloginscreen.entities.Rental

data class RentalUiState (
    val heightCm: String = "",
    val age: String = "",
    val days: String = "",
    val equipment: String = "",
    val waverSigned: Boolean = false,

    val bookingId: String? = null,
    val isBookingInProgress: Boolean = false,
    val errorMessage: String? = null,
    val userRentals: List<Rental> = emptyList()
)