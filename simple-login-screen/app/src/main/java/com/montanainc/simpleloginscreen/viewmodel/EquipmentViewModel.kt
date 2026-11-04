package com.montanainc.simpleloginscreen.viewmodel

import androidx.lifecycle.ViewModel
import com.montanainc.simpleloginscreen.data.EquipmentRepository
import com.montanainc.simpleloginscreen.screens.UserUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EquipmentViewModel(private val equipmentRepo: EquipmentRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    //val uiState: StateFlow<EquipmentUiState> = _uiState.asStateFlow()

}