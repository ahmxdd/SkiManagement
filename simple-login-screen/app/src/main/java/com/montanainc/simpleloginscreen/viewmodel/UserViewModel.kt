package com.montanainc.simpleloginscreen.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.montanainc.simpleloginscreen.data.UserRepository
import com.montanainc.simpleloginscreen.entities.User
import com.montanainc.simpleloginscreen.screens.UserDetails
import com.montanainc.simpleloginscreen.screens.UserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.printStackTrace

class UserViewModel(private val userRepo: UserRepository) :  ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState.asStateFlow()




    // Called when the user clicks on "Register"

    fun registerUser(onSuccess: () -> Unit) {

            viewModelScope.launch {
                val details = uiState.value.userDetails
                val user = User(
                    firstName = details.firstName,
                    lastName = details.lastName,
                    email = details.email,
                    phoneNumber = details.phoneNumber,
                    password = details.password
                )
                if (userRepo.getUserByEmail(details.email, details.password) == null) {
                    try {
                        userRepo.insertUser(user)
                        onSuccess()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
    }

    fun updateUiState(newDetails: UserDetails) {
        _uiState.update { currentState ->

            currentState.copy(userDetails = newDetails)
        }
    }
    fun loginUser(onSuccess: (User) -> Unit) {
        viewModelScope.launch {
            val details = uiState.value.userDetails
            val newUser = userRepo.getUserByEmail(details.email, details.password)
            if (newUser != null) {
                try {
                    _uiState.update { it.copy(loggedInUser = newUser) }
                    onSuccess(newUser)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else {
                Log.d("Userviewmodel", "User is not valid")
            }
        }
    }
    fun resetPassword(newPassword: String, email: String) {
        viewModelScope.launch {
            userRepo.updatePassword(newPassword, email)
        }
    }

}