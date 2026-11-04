package com.montanainc.simpleloginscreen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montanainc.simpleloginscreen.viewmodel.AppViewModelProvider
import com.montanainc.simpleloginscreen.viewmodel.RentalViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRentalsScreen(userId: Int, onBack: () -> Unit, viewModel: RentalViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    // Sample data until the backend is hooked up
    val rentals by viewModel.getRentalForUser(userId).collectAsState(initial = emptyList())

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
                            Text("Equipment ${rental.equipmentId}", style = MaterialTheme.typography.titleMedium)
                            Text("Booking ${rental.bookingId} · ${rental.status}")
                        }
                        if (rental.status != "CANCELLED") {
                            OutlinedButton(onClick = {
                                viewModel.cancelRental(rentalId = rental.id)
                            }) { Text("Cancel") }
                        }

                }
            }
        }
    }
}
}