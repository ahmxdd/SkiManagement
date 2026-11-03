package com.montanainc.simpleloginscreen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.montanainc.simpleloginscreen.entities.Equipment
import java.util.UUID
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(equipment: Equipment?, onDone: () -> Unit) {
    var height by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var days by remember { mutableStateOf("1") }
    var waiverSigned by remember { mutableStateOf(false) }
    var showWaiver by remember { mutableStateOf(false) }
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = waiverSigned,
                        onCheckedChange = { if (it) showWaiver = true else waiverSigned = false }
                    )
                    Text("I have read and agree to the rental waiver")
                }
                if (showWaiver) {
                    AlertDialog(
                        onDismissRequest = { showWaiver = false },
                        title = { Text("Rental Waiver") },
                        text = {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                Text(
                                    "I acknowledge that skiing and snowboarding carry inherent risks of injury. " +
                                            "I agree to use the equipment responsibly, return it on time and in the same " +
                                            "condition, and accept responsibility for loss or damage. " +
                                            "[Replace with your team's real waiver wording.]"
                                )
                            }
                        },
                        confirmButton = { TextButton(onClick = { waiverSigned = true; showWaiver = false }) { Text("I Agree") } },
                        dismissButton = { TextButton(onClick = { showWaiver = false }) { Text("Cancel") } }
                    )
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