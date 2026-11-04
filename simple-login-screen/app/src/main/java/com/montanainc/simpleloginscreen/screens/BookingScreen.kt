package com.montanainc.simpleloginscreen.screens

import android.content.Intent
import android.provider.CalendarContract
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montanainc.simpleloginscreen.entities.Equipment
import com.montanainc.simpleloginscreen.entities.User
import com.montanainc.simpleloginscreen.viewmodel.AppViewModelProvider
import com.montanainc.simpleloginscreen.viewmodel.RentalViewModel
import com.montanainc.simpleloginscreen.viewmodel.UserViewModel
import java.util.Calendar
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(equipment: Equipment?, userId: Int, userEmail: String, onDone: () -> Unit, viewModel: RentalViewModel = viewModel(factory = AppViewModelProvider.Factory),
                  userViewModel: UserViewModel= viewModel(factory = AppViewModelProvider.Factory)) {
    val rentalUiState by viewModel.uiState.collectAsState()
    val height = rentalUiState.heightCm
    val age = rentalUiState.age
    val days = rentalUiState.days
    val waiverSigned = rentalUiState.waverSigned
    val bookingId = rentalUiState.bookingId
    val equipmentRental = rentalUiState.equipment



    val cost = (equipment?.pricePerDay ?: 0.0) * (days.toIntOrNull() ?: 0)

    Scaffold(topBar = { TopAppBar(title = { Text("Book ${equipment?.name ?: "Equipment"}") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (bookingId == null) {
                OutlinedTextField(height, {viewModel.updateHeight(it) }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(age, {viewModel.updateAge(it)}, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(days, { viewModel.updateDays(it) }, label = { Text("Number of days") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(equipmentRental, { viewModel.updateEquipment(it) }, label = { Text("Equipment you will already bring") }, modifier = Modifier.fillMaxWidth())
                Text("Expected cost: $${"%.2f".format(cost)}")
                Row {
                    Checkbox(checked = waiverSigned, onCheckedChange = { viewModel.updateWaiver(it) })
                    Text("I have read and agree to the rental waiver", Modifier.padding(top = 12.dp))
                }
                Button(
                    onClick = { viewModel.registerRental(onSuccess =  { }, userId, equipment?.id ?: 0, userEmail
                    )},
                    enabled = waiverSigned && height.isNotBlank() && age.isNotBlank() && (days.toIntOrNull() ?: 0) > 0,
                    modifier = Modifier.fillMaxWidth()

                ) { Text("Confirm Booking") }
            } else {
                val context = LocalContext.current

                Text("Booking confirmed!", style = MaterialTheme.typography.headlineSmall)
                Text("Your booking ID: $bookingId")
                Text("Present this ID at the shop to pay.")
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_INSERT).apply {
                            data = CalendarContract.Events.CONTENT_URI
                            putExtra(CalendarContract.Events.TITLE, "Rental: ${equipment?.name}")
                            putExtra(
                                CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                System.currentTimeMillis()
                            )
                            putExtra(
                                CalendarContract.EXTRA_EVENT_END_TIME,
                                System.currentTimeMillis() + (days.toLongOrNull() ?: 1L) * 86400000L
                            )
                        }
                        context.startActivity(intent)
                    }
                ) {
                        Text("Add to Google Calendar")
                }
                Button(onClick =  onDone , modifier = Modifier.fillMaxWidth()) { Text("Back to equipment") }
            }
        }
    }
}