@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.montanainc.simpleloginscreen.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.montanainc.simpleloginscreen.applyFixedDiscount
import com.montanainc.simpleloginscreen.database.AppDatabase
import com.montanainc.simpleloginscreen.entities.BookingExtra
import com.montanainc.simpleloginscreen.entities.Discount
import com.montanainc.simpleloginscreen.entities.Equipment
import com.montanainc.simpleloginscreen.money
import com.montanainc.simpleloginscreen.sendEmail
import com.montanainc.simpleloginscreen.validateDiscountCode
import com.montanainc.simpleloginscreen.viewmodel.RentalUiState
import kotlinx.coroutines.launch


class BookingExtrasState {
    var codeInput by mutableStateOf("")
    var discount by mutableStateOf<Discount?>(null)
    var message by mutableStateOf<String?>(null)
}

@Composable
fun rememberBookingExtras(): BookingExtrasState = remember { BookingExtrasState() }

/** Discount code field + "Apply" button + the discounted total. Place it under the "Expected cost" line. */
@Composable
fun DiscountSection(extras: BookingExtrasState, subtotal: Double) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dao = remember { AppDatabase.getDatabase(context).discountDao() }
    val total = applyFixedDiscount(subtotal, extras.discount)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                extras.codeInput,
                {
                    extras.codeInput = it
                    extras.discount = null
                    extras.message = null
                },
                label = { Text("Discount code") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = {
                    scope.launch {
                        val found = dao.findActive(extras.codeInput.trim().uppercase())
                        val error = validateDiscountCode(found)
                        extras.discount = if (error == null) found else null
                        extras.message = error ?: "Code applied!"
                    }
                },
                enabled = extras.codeInput.isNotBlank()
            ) { Text("Apply") }
        }
        extras.message?.let { Text(it) }
        extras.discount?.let {
            Text("Discount (${it.code}): -${money(subtotal - total)}")
            Text("Total after discount: ${money(total)}", style = MaterialTheme.typography.titleMedium)
        }
    }
}

/** Shows the final price on the "Booking confirmed" part of the screen. */
@Composable
fun TotalToPay(extras: BookingExtrasState, subtotal: Double) {
    Text("Total to pay at the shop: ${money(applyFixedDiscount(subtotal, extras.discount))}")
}

/** Saves the final price when the booking is confirmed, and emails the customer if they leave without confirming. */
@Composable
fun BookingExtrasEffects(
    extras: BookingExtrasState,
    uiState: RentalUiState,
    subtotal: Double,
    userEmail: String,
    itemName: String?
) {
    val context = LocalContext.current
    val bookingExtraDao = remember { AppDatabase.getDatabase(context).bookingExtraDao() }
    val total = applyFixedDiscount(subtotal, extras.discount)

    LaunchedEffect(uiState.bookingId) {
        val id = uiState.bookingId
        if (id != null) bookingExtraDao.saveExtra(BookingExtra(bookingId = id, totalCost = total, lateFee = 0.0))
    }

    val latestState = rememberUpdatedState(uiState)
    val latestName = rememberUpdatedState(itemName)
    DisposableEffect(Unit) {
        onDispose {
            val s = latestState.value
            val startedForm = s.heightCm.isNotBlank() || s.age.isNotBlank() || s.days.isNotBlank()
            val rotating = context.findActivity()?.isChangingConfigurations == true
            if (s.bookingId == null && startedForm && !rotating) {
                sendEmail(
                    userEmail,
                    "You left a booking unfinished",
                    "You started booking ${latestName.value ?: "your rental"} but didn't finish. " +
                            "Come back to the app to complete your booking before it's gone!"
                )
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    var c: Context? = this
    while (c is ContextWrapper) {
        if (c is Activity) return c
        c = c.baseContext
    }
    return null
}

/** Optional: loads one equipment item from the database so items added by the manager can be booked too. */
@Composable
fun rememberEquipment(id: Int?): Equipment? {
    val context = LocalContext.current
    val flow = remember(id) { AppDatabase.getDatabase(context).bookingExtraDao().equipmentById(id ?: -1) }
    val item by flow.collectAsState(initial = null)
    return item
}
