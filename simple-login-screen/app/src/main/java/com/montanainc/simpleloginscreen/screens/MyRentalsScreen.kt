package com.montanainc.simpleloginscreen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class RentalDisplay(val bookingId: String, val itemName: String, var status: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRentalsScreen(onBack: () -> Unit) {
    // Sample data until the backend is hooked up
    val rentals = remember {
        mutableStateListOf(
            RentalDisplay("A1B2C3D4", "Rossignol Skis", "CONFIRMED"),
            RentalDisplay("E5F6G7H8", "Burton Snowboard", "PENDING")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Rentals") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(rentals) { rental ->
                Card(Modifier.fillMaxWidth()) {
                    Row(
                        Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(rental.itemName, style = MaterialTheme.typography.titleMedium)
                            Text("Booking ${rental.bookingId} · ${rental.status}")
                        }
                        if (rental.status != "CANCELLED") {
                            OutlinedButton(onClick = {
                                val i = rentals.indexOf(rental)
                                rentals[i] = rental.copy(status = "CANCELLED")
                            }) { Text("Cancel") }
                        }

                }
            }
        }
    }
}
}