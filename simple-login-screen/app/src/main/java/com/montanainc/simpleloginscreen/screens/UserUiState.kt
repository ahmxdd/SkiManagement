package com.montanainc.simpleloginscreen.screens

import com.montanainc.simpleloginscreen.entities.User

data class UserUiState(
    val userDetails: UserDetails = UserDetails(),
    val isEntryValid: Boolean = false,
    val loggedInUser: User? = null
)
