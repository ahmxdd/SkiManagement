package com.montanainc.simpleloginscreen.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.montanainc.simpleloginscreen.EquipmentDao
import com.montanainc.simpleloginscreen.RentalDao
import com.montanainc.simpleloginscreen.database.AppDatabase
import com.montanainc.simpleloginscreen.RentalDetails
import com.montanainc.simpleloginscreen.entities.Equipment
import com.montanainc.simpleloginscreen.entities.Rental
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DbViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db = AppDatabase.getDatabase(context.applicationContext)
        return when {
            modelClass.isAssignableFrom(EquipmentViewModel::class.java) ->
                EquipmentViewModel(db.equipmentDao())
            modelClass.isAssignableFrom(ManagerViewModel::class.java) ->
                ManagerViewModel(db.equipmentDao(), db.rentalDao())
            else -> throw IllegalArgumentException("Unknown ViewModel")
        } as T
    }
}

// Customer side: search + filters (C7M-17, 18, 45)
class EquipmentViewModel(private val dao: EquipmentDao) : ViewModel() {
    val query = MutableStateFlow("")
    val availableOnly = MutableStateFlow(false)
    val type = MutableStateFlow<String?>(null)
    val brand = MutableStateFlow<String?>(null)

    private val all = dao.getAllVisible() // already hides broken items (C7M-41)

    val brands: StateFlow<List<String>> = all
        .map { list -> list.map { it.brand }.distinct().sorted() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val items: StateFlow<List<Equipment>> =
        combine(all, query, availableOnly, type, brand) { list, q, avail, t, b ->
            list.filter {
                it.name.contains(q, ignoreCase = true) &&
                        (!avail || it.status == "AVAILABLE") &&
                        (t == null || it.type == t) &&
                        (b == null || it.brand == b)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Fills the empty database with the demo equipment the first time
        viewModelScope.launch {
            if (dao.count() == 0) sampleEquipment.forEach { dao.insert(it.copy(id = 0)) }
        }
    }
}

// Manager side: reads and writes the real database
class ManagerViewModel(
    private val equipmentDao: EquipmentDao,
    private val rentalDao: RentalDao
) : ViewModel() {

    val inventory: StateFlow<List<Equipment>> = equipmentDao.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rentals: StateFlow<List<RentalDetails>> = rentalDao.getAllWithDetails()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Demo rentals so the manager tabs have something to show until bookings save
        viewModelScope.launch {
            if (rentalDao.count() == 0) {
                val now = System.currentTimeMillis()
                val day = 86_400_000L
                rentalDao.insert(Rental(
                    bookingId = "A1B2C3D4",
                    userId = 0,
                    equipmentId = 1,
                    startDate = now,
                    endDate = now + day,
                    status = "RESERVED",
                    specialRequest = "TODO()",
                    equipmentInfo = "TODO()"
                ))
                rentalDao.insert(Rental(
                    bookingId = "J9K0L1M2",
                    userId = 0,
                    equipmentId = 3,
                    startDate = now - 3 * day,
                    endDate = now - day,
                    status = "OVERDUE",
                    specialRequest = "TODO()",
                    equipmentInfo = "TODO()"
                ))
            }
        }
    }

    // C7M-41: broken flag
    fun toggleBroken(item: Equipment) = viewModelScope.launch {
        equipmentDao.update(item.copy(isBroken = !item.isBroken))
    }

    // Check-in: customer arrives and takes the gear
    fun checkIn(r: RentalDetails) = viewModelScope.launch {
        rentalDao.setStatus(r.id, "CHECKED_OUT")
        equipmentDao.setStatus(r.equipmentId, "RENTED")
    }

    // C7M-26: manager confirms the return (backend's C7M-20 does this same DB update)
    fun confirmReturn(r: RentalDetails) = viewModelScope.launch {
        rentalDao.setStatus(r.id, "RETURNED")
        equipmentDao.setStatus(r.equipmentId, "AVAILABLE")
    }

    fun addEquipment(e: Equipment) = viewModelScope.launch { equipmentDao.insert(e) }
}