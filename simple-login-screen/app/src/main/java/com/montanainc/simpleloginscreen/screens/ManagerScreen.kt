@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.montanainc.simpleloginscreen.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montanainc.simpleloginscreen.RentalDetails
import com.montanainc.simpleloginscreen.entities.Equipment

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ManagerScreen(onBack: () -> Unit) {
    val vm: ManagerViewModel = viewModel(factory = DbViewModelFactory(LocalContext.current))
    val inventory by vm.inventory.collectAsState()
    val rentals by vm.rentals.collectAsState()
    var tab by remember { mutableStateOf(0) }
    val tabs = listOf("Inventory", "Rentals", "Returns", "Add")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manager Dashboard") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = tab) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title) })
                }
            }
            when (tab) {
                0 -> InventoryTab(inventory, onToggleBroken = { vm.toggleBroken(it) })
                1 -> RentalsTab(rentals, onCheckIn = { vm.checkIn(it) })
                2 -> ReturnsTab(rentals, onReturn = { vm.confirmReturn(it) })
                3 -> AddEquipmentTab(onSave = { vm.addEquipment(it) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InventoryTab(inventory: List<Equipment>, onToggleBroken: (Equipment) -> Unit) {
    val available = inventory.count { it.status == "AVAILABLE" && !it.isBroken }
    val rented = inventory.count { it.status == "RENTED" }
    val broken = inventory.count { it.isBroken }

    Column(Modifier.padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Text("Available: $available")
            Text("Rented: $rented")
            Text("Broken: $broken")
        }
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(inventory, key = { it.id }) { item ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(item.name, style = MaterialTheme.typography.titleMedium)
                        Text("${item.type} · Size ${item.size} · ${item.brand} · ${item.skillLevel}")
                        Text(if (item.isBroken) "BROKEN (hidden from customers)" else item.status)
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = { onToggleBroken(item) }) {
                            Text(if (item.isBroken) "Mark Repaired" else "Mark Broken")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RentalsTab(rentals: List<RentalDetails>, onCheckIn: (RentalDetails) -> Unit) {
    val active = rentals.filter { it.status != "RETURNED" && it.status != "CANCELLED" }
    if (active.isEmpty()) {
        Text("No active rentals.", Modifier.padding(16.dp))
        return
    }
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(active, key = { it.id }) { r ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(r.itemName, style = MaterialTheme.typography.titleMedium)
                    Text("Customer: ${r.customer}")
                    Text("Booking ${r.bookingId} · ${r.status}")
                    if (r.status == "RESERVED") {
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { onCheckIn(r) }) { Text("Check In Customer") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReturnsTab(rentals: List<RentalDetails>, onReturn: (RentalDetails) -> Unit) {
    val outNow = rentals.filter { it.status == "CHECKED_OUT" || it.status == "OVERDUE" }
    if (outNow.isEmpty()) {
        Text("No equipment waiting to be returned.", Modifier.padding(16.dp))
        return
    }
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(outNow, key = { it.id }) { r ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(r.itemName, style = MaterialTheme.typography.titleMedium)
                    Text("Customer: ${r.customer}")
                    Text(if (r.status == "OVERDUE") "OVERDUE: late fee applies" else "Currently out")
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { onReturn(r) }) { Text("Confirm Return") }
                }
            }
        }
    }
}

@Composable
private fun AddEquipmentTab(onSave: (Equipment) -> Unit) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var size by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var skill by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(name, { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(type, { type = it }, label = { Text("Type (SKI, SNOWBOARD, BOOTS, HELMET, GOGGLES)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(size, { size = it }, label = { Text("Size") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(brand, { brand = it }, label = { Text("Brand") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(skill, { skill = it }, label = { Text("Skill level (BEGINNER, INTERMEDIATE, ADVANCED)") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(price, { price = it }, label = { Text("Price per day") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                onSave(
                    Equipment(
                        name = name.trim(),
                        type = type.trim().uppercase(),
                        size = size.trim(),
                        brand = brand.trim(),
                        skillLevel = skill.trim().uppercase(),
                        pricePerDay = price.toDouble()
                    )
                )
                message = "Saved '$name' to the database."
                name = ""; type = ""; size = ""; brand = ""; skill = ""; price = ""
            },
            enabled = name.isNotBlank() && price.toDoubleOrNull() != null,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Equipment") }
        message?.let { Text(it) }
    }
}