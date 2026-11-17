package com.montanainc.simpleloginscreen.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.montanainc.simpleloginscreen.data.UserApplication

object AppViewModelProvider {
    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            UserViewModel(
                userApplication().container.userRepository
            )
        }
        initializer {
            RentalViewModel(
                userApplication().container.rentalRepository,
                userApplication().container.equipmentRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [UserApplication].
 */
fun CreationExtras.userApplication(): UserApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as UserApplication)