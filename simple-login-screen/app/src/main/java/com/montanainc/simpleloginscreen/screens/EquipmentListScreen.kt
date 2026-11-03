package com.montanainc.simpleloginscreen.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montanainc.simpleloginscreen.entities.Equipment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentListScreen(
    onBook: (Equipment) -> Unit,
    onMyRentals: () -> Unit,
    onManager: () -> Unit = {}
) {
    val vm: EquipmentViewModel = viewModel(factory = DbViewModelFactory(LocalContext.current))
    val items by vm.items.collectAsState()
    val brands by vm.brands.collectAsState()
    val query by vm.query.collectAsState()
    val availableOnly by vm.availableOnly.collectAsState()
    val type by vm.type.collectAsState()
    val brand by vm.brand.collectAsState()
    var brandMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rental Equipment") },
                actions = {
                    TextButton(onClick = onManager) { Text("Manager") }
                    TextButton(onClick = onMyRentals) { Text("My Rentals") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            OutlinedTextField(
                value = query,
                onValueChange = { vm.query.value = it },
                label = { Text("Search equipment") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = availableOnly,
                    onClick = { vm.availableOnly.value = !availableOnly },
                    label = { Text("Available only") }
                )
                listOf("SKI", "SNOWBOARD", "BOOTS", "HELMET", "GOGGLES").forEach { t ->
                    FilterChip(
                        selected = type == t,
                        onClick = { vm.type.value = if (type == t) null else t },
                        label = { Text(t) }
                    )
                }
            }
            Box {
                OutlinedButton(onClick = { brandMenu = true }) { Text(brand ?: "All brands") }
                DropdownMenu(expanded = brandMenu, onDismissRequest = { brandMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("All brands") },
                        onClick = { vm.brand.value = null; brandMenu = false }
                    )
                    brands.forEach { b ->
                        DropdownMenuItem(
                            text = { Text(b) },
                            onClick = { vm.brand.value = b; brandMenu = false }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            if (items.isEmpty()) Text("No equipment matches your filters.")
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.id }) { item ->
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