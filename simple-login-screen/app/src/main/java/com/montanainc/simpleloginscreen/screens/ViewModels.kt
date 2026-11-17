package com.montanainc.simpleloginscreen.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.montanainc.simpleloginscreen.BannerDao
import com.montanainc.simpleloginscreen.EquipmentDao
import com.montanainc.simpleloginscreen.FeedbackDetails
import com.montanainc.simpleloginscreen.RentalSchedule
import com.montanainc.simpleloginscreen.RevenueRow
import com.montanainc.simpleloginscreen.WaitlistDao
import com.montanainc.simpleloginscreen.calculateLateFee
import com.montanainc.simpleloginscreen.database.AppDatabase
import com.montanainc.simpleloginscreen.entities.AppBanner
import com.montanainc.simpleloginscreen.entities.BookingExtra
import com.montanainc.simpleloginscreen.entities.Discount
import com.montanainc.simpleloginscreen.entities.Equipment
import com.montanainc.simpleloginscreen.entities.Feedback
import com.montanainc.simpleloginscreen.entities.Rental
import com.montanainc.simpleloginscreen.entities.WaitlistEntry
import com.montanainc.simpleloginscreen.money
import com.montanainc.simpleloginscreen.sendEmail
import kotlinx.coroutines.flow.Flow
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
                EquipmentViewModel(db.equipmentDao(), db.waitlistDao(), db.bannerDao())
            modelClass.isAssignableFrom(ManagerViewModel::class.java) ->
                ManagerViewModel(db)
            else -> throw IllegalArgumentException("Unknown ViewModel")
        } as T
    }
}

class EquipmentViewModel(
    private val dao: EquipmentDao,
    private val waitlistDao: WaitlistDao,
    bannerDao: BannerDao
) : ViewModel() {
    val query = MutableStateFlow("")
    val availableOnly = MutableStateFlow(false)
    val type = MutableStateFlow<String?>(null)
    val brand = MutableStateFlow<String?>(null)

    private val all = dao.getAllVisible()

    val banner: StateFlow<AppBanner?> = bannerDao.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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
        viewModelScope.launch {
            if (dao.count() == 0) sampleEquipment.forEach { dao.insert(it.copy(id = 0)) }
        }
    }

    fun joinWaitlist(item: Equipment, userId: Int, email: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            if (waitlistDao.alreadyQueued(item.id, userId) > 0) {
                onResult("You are already on the waitlist for ${item.name}")
            } else {
                waitlistDao.insert(WaitlistEntry(equipmentId = item.id, userId = userId, email = email))
                onResult("Added to the waitlist for ${item.name}. We'll email you when it's back.")
            }
        }
    }
}

class ManagerViewModel(private val db: AppDatabase) : ViewModel() {
    private val equipmentDao = db.equipmentDao()
    private val rentalDao = db.rentalDao()
    private val feedbackDao = db.feedbackDao()
    private val waitlistDao = db.waitlistDao()
    private val discountDao = db.discountDao()
    private val bannerDao = db.bannerDao()
    private val bookingExtraDao = db.bookingExtraDao()

    private fun <T> Flow<T>.asState(initial: T): StateFlow<T> =
        stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), initial)

    val inventory: StateFlow<List<Equipment>> = equipmentDao.getAll().asState(emptyList())
    val rentals: StateFlow<List<RentalSchedule>> = bookingExtraDao.getSchedule().asState(emptyList())
    val revenueRows: StateFlow<List<RevenueRow>> = bookingExtraDao.getRevenueRows().asState(emptyList())
    val feedback: StateFlow<List<FeedbackDetails>> = feedbackDao.getAllWithItem().asState(emptyList())
    val waitlist: StateFlow<List<WaitlistEntry>> = waitlistDao.getWaiting().asState(emptyList())
    val discounts: StateFlow<List<Discount>> = discountDao.getAll().asState(emptyList())
    val banner: StateFlow<AppBanner?> = bannerDao.observe().asState(null)

    val message = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val day = 86_400_000L
            if (bookingExtraDao.countDemo() == 0) {
                rentalDao.insert(Rental(bookingId = "DEMO-UP", userId = -1, equipmentId = 1,
                    startDate = now + day, endDate = now + 2 * day, status = "CONFIRMED"))
                rentalDao.insert(Rental(bookingId = "DEMO-OVR", userId = -1, equipmentId = 3,
                    startDate = now - 3 * day, endDate = now - day, status = "OVERDUE"))
                for (i in 1..12) {
                    rentalDao.insert(Rental(
                        bookingId = "HIST%02d".format(i), userId = -1, equipmentId = 1 + i % 3,
                        startDate = now - i * day, endDate = now - (i - 2) * day, status = "RETURNED"
                    ))
                }
            }
            if (feedbackDao.count() == 0) {
                feedbackDao.insert(Feedback(equipmentId = 1, userId = -1, rating = 5, comment = "Great fit, very comfortable."))
                feedbackDao.insert(Feedback(equipmentId = 2, userId = -1, rating = 3, comment = "Bindings were a bit stiff."))
            }
            if (discountDao.count() == 0) {
                discountDao.upsert(Discount(code = "WELCOME10", amount = 10.0))
            }
        }
    }

    fun toggleBroken(item: Equipment) = viewModelScope.launch {
        equipmentDao.update(item.copy(isBroken = !item.isBroken))
    }

    fun checkIn(r: RentalSchedule) = viewModelScope.launch {
        rentalDao.setStatus(r.id, "CHECKED_OUT")
        equipmentDao.setStatus(r.equipmentId, "RENTED")
        message.value = "${r.customer} checked in with ${r.itemName}."
    }

    fun confirmReturn(r: RentalSchedule) = viewModelScope.launch {
        val fee = calculateLateFee(r.endDate, r.pricePerDay)
        rentalDao.setStatus(r.id, "RETURNED")
        if (bookingExtraDao.setLateFee(r.bookingId, fee) == 0) {
            bookingExtraDao.saveExtra(BookingExtra(bookingId = r.bookingId, totalCost = r.totalCost, lateFee = fee))
        }
        equipmentDao.setStatus(r.equipmentId, "AVAILABLE")

        var msg = "Return confirmed for ${r.itemName}."
        if (fee > 0) msg += " Late fee: ${money(fee)}."

        val next = waitlistDao.firstInQueue(r.equipmentId)
        if (next != null) {
            sendEmail(
                next.email,
                "${r.itemName} is available",
                "Good news! ${r.itemName} that you were waiting for is available to book now."
            )
            waitlistDao.markNotified(next.id)
            msg += " Waitlist email sent to ${next.email}."
        }
        message.value = msg
    }

    fun addEquipment(e: Equipment) = viewModelScope.launch { equipmentDao.insert(e) }

    fun addDiscount(code: String, amount: Double) = viewModelScope.launch {
        discountDao.upsert(Discount(code = code.trim().uppercase(), amount = amount))
        message.value = "Discount ${code.trim().uppercase()} saved."
    }

    fun toggleDiscount(d: Discount) = viewModelScope.launch {
        discountDao.setActive(d.code, !d.active)
    }

    fun saveBanner(text: String, active: Boolean) = viewModelScope.launch {
        bannerDao.save(AppBanner(id = 1, message = text, isActive = active))
        message.value = if (active) "Banner is live on the customer homepage." else "Banner saved but hidden."
    }
}
