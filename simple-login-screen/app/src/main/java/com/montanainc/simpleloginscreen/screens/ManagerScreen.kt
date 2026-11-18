@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.montanainc.simpleloginscreen.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montanainc.simpleloginscreen.RentalSchedule
import com.montanainc.simpleloginscreen.calculateLateFee
import com.montanainc.simpleloginscreen.entities.Equipment
import com.montanainc.simpleloginscreen.money

@Composable
fun ManagerScreen(onBack: () -> Unit) {
    val vm: ManagerViewModel = viewModel(factory = DbViewModelFactory(LocalContext.current))
    val inventory by vm.inventory.collectAsState()
    val rentals by vm.rentals.collectAsState()
    val revenueRows by vm.revenueRows.collectAsState()
    val feedback by vm.feedback.collectAsState()
    val waitlist by vm.waitlist.collectAsState()
    val discounts by vm.discounts.collectAsState()
    val banner by vm.banner.collectAsState()
    val message by vm.message.collectAsState()

    var tab by remember { mutableStateOf(0) }
    val tabs = listOf(
        "Inventory", "Rentals", "Returns", "Schedule", "Revenue",
        "Feedback", "Waitlist", "Promos", "Map", "Add"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manager Dashboard") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            // Scrollable row of chips instead of a tab bar, since there are 10 sections
            Row(
                Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { i, title ->
                    FilterChip(selected = tab == i, onClick = { tab = i }, label = { Text(title) })
                }
            }
            message?.let {
                Card(Modifier.fillMaxWidth().padding(horizontal = 12.dp)) {
                    Row(
                        Modifier.padding(start = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(it, Modifier.weight(1f))
                        TextButton(onClick = { vm.message.value = null }) { Text("OK") }
                    }
                }
            }
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (tab) {
                    0 -> InventoryTab(inventory, onToggleBroken = { vm.toggleBroken(it) })
                    1 -> RentalsTab(rentals, onCheckIn = { vm.checkIn(it) })
                    2 -> ReturnsTab(rentals, onReturn = { vm.confirmReturn(it) })
                    3 -> ScheduleTab(rentals)
                    4 -> RevenueTab(revenueRows)
                    5 -> FeedbackTab(feedback)
                    6 -> WaitlistTab(waitlist, inventory)
                    7 -> PromosTab(
                        discounts = discounts,
                        banner = banner,
                        onAddDiscount = { code, amount -> vm.addDiscount(code, amount) },
                        onToggleDiscount = { vm.toggleDiscount(it) },
                        onSaveBanner = { text, active -> vm.saveBanner(text, active) }
                    )
                    8 -> TrailMapTab()
                    9 -> AddEquipmentTab(onSave = { vm.addEquipment(it) })
                }
            }
        }
    }
}

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
private fun RentalsTab(rentals: List<RentalSchedule>, onCheckIn: (RentalSchedule) -> Unit) {
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
                    if (r.equipment.isNotBlank()) Text("Equipment Owned: ${r.equipment}")
                    if (r.request.isNotBlank()) Text("Special Request: ${r.request}")
                    Text("Booking ${r.bookingId} · ${r.status} · ${money(r.totalCost)}")
                    if (r.status in CHECK_IN_STATUSES) {
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { onCheckIn(r) }) { Text("Check In Customer") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReturnsTab(rentals: List<RentalSchedule>, onReturn: (RentalSchedule) -> Unit) {
    val outNow = rentals.filter { it.status == "CHECKED_OUT" || it.status == "OVERDUE" }
    if (outNow.isEmpty()) {
        Text("No equipment waiting to be returned.", Modifier.padding(16.dp))
        return
    }
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(outNow, key = { it.id }) { r ->
            val fee = calculateLateFee(r.endDate, r.pricePerDay)
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(r.itemName, style = MaterialTheme.typography.titleMedium)
                    Text("Customer: ${r.customer}")
                    Text(
                        if (fee > 0) "OVERDUE: late fee of ${money(fee)} will be added"
                        else "Due back ${formatDate(r.endDate)}"
                    )
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
