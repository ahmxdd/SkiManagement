package com.montanainc.simpleloginscreen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.montanainc.simpleloginscreen.entities.Equipment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentListScreen(
    equipment: List<Equipment>,
    onBook: (Equipment) -> Unit,
    onMyRentals: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = equipment.filter { it.name.contains(query, ignoreCase = true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rental Equipment") },
                actions = { TextButton(onClick = onMyRentals) { Text("My Rentals") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search equipment") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text("${item.type} · Size ${item.size} · ${item.skillLevel}")
                                Text("$${item.pricePerDay}/day")
                            }
                            Button(
                                onClick = { onBook(item) },
                                enabled = item.status == "AVAILABLE"
                            ) { Text("Book") }
                        }
                    }
                }
            }
        }
    }
}

val sampleEquipment = listOf(
    Equipment(1, "Rossignol Skis", "SKI", "160", "Rossignol", "BEGINNER", 45.0),
    Equipment(2, "Burton Snowboard", "SNOWBOARD", "152", "Burton", "INTERMEDIATE", 50.0),
    Equipment(3, "Atomic Skis", "SKI", "170", "Atomic", "ADVANCED", 60.0, status = "RENTED")
)