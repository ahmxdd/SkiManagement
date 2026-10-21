package com.montanainc.simpleloginscreen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.montanainc.simpleloginscreen.entities.Equipment
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(equipment: Equipment?, onDone: () -> Unit) {
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("1") }
    var waiverSigned by remember { mutableStateOf(false) }
    var bookingId by remember { mutableStateOf<String?>(null) }

    val cost = (equipment?.pricePerDay ?: 0.0) * (days.toIntOrNull() ?: 0)

    Scaffold(topBar = { TopAppBar(title = { Text("Book ${equipment?.name ?: "Equipment"}") }) }) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (bookingId == null) {
                OutlinedTextField(height, { height = it }, label = { Text("Height (cm)") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(age, { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(days, { days = it }, label = { Text("Number of days") }, modifier = Modifier.fillMaxWidth())
                Text("Expected cost: $${"%.2f".format(cost)}")
                Row {
                    Checkbox(checked = waiverSigned, onCheckedChange = { waiverSigned = it })
                    Text("I have read and agree to the rental waiver", Modifier.padding(top = 12.dp))
                }
                Button(
                    onClick = { bookingId = UUID.randomUUID().toString().take(8).uppercase() },
                    enabled = waiverSigned && height.isNotBlank() && age.isNotBlank() && (days.toIntOrNull() ?: 0) > 0,
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Confirm Booking") }
            } else {
                Text("Booking confirmed!", style = MaterialTheme.typography.headlineSmall)
                Text("Your booking ID: $bookingId")
                Text("Present this ID at the shop to pay.")
                Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) { Text("Back to equipment") }
            }
        }
    }
}