package com.montanainc.simpleloginscreen.screens

data class UserUiState(
    val userDetails: UserDetails = UserDetails(),
    val isEntryValid: Boolean = false
)
