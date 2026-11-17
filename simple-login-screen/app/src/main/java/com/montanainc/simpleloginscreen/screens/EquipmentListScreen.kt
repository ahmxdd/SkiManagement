package com.montanainc.simpleloginscreen.screens

import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.montanainc.simpleloginscreen.database.AppDatabase
import com.montanainc.simpleloginscreen.entities.Equipment
import com.montanainc.simpleloginscreen.entities.Feedback
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentListScreen(
    onBook: (Equipment) -> Unit,
    onMyRentals: () -> Unit,
    onManager: () -> Unit = {},
    onAbout: () -> Unit = {},
    userId: Int = 0,
    userEmail: String = ""
) {
    val context = LocalContext.current
    val vm: EquipmentViewModel = viewModel(factory = DbViewModelFactory(context))
    val items by vm.items.collectAsState()
    val brands by vm.brands.collectAsState()
    val query by vm.query.collectAsState()
    val availableOnly by vm.availableOnly.collectAsState()
    val type by vm.type.collectAsState()
    val brand by vm.brand.collectAsState()
    val banner by vm.banner.collectAsState()
    var brandMenu by remember { mutableStateOf(false) }

    // C7M-39: rating dialog state
    val scope = rememberCoroutineScope()
    val feedbackDao = remember { AppDatabase.getDatabase(context).feedbackDao() }
    var rateItem by remember { mutableStateOf<Equipment?>(null) }
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rental Equipment") },
                actions = {
                    TextButton(onClick = onAbout) { Text("About")}
                    TextButton(onClick = onManager) { Text("Manager") }
                    TextButton(onClick = onMyRentals) { Text("My Rentals") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            // C7M-55: global banner set by the manager
            banner?.let { b ->
                if (b.isActive && b.message.isNotBlank()) {
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Text(b.message, Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }
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
                            Column(Modifier.weight(1f)) {
                                Text(item.name, style = MaterialTheme.typography.titleMedium)
                                Text("${item.type} · Size ${item.size} · ${item.skillLevel}")
                                Text("$${item.pricePerDay}/day")
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                if (item.status == "AVAILABLE") {
                                    Button(onClick = { onBook(item) }) { Text("Book") }
                                } else {
                                    // C7M-46: rented items get a waitlist button instead of a disabled Book button
                                    OutlinedButton(onClick = {
                                        vm.joinWaitlist(item, userId, userEmail) { msg ->
                                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                        }
                                    }) { Text("Join waitlist") }
                                }
                                TextButton(onClick = {
                                    rating = 5
                                    comment = ""
                                    rateItem = item
                                }) { Text("Rate") }
                            }
                        }
                    }
                }
            }
        }
    }

    rateItem?.let { item ->
        AlertDialog(
            onDismissRequest = { rateItem = null },
            title = { Text("Rate ${item.name}") },
            text = {
                Column {
                    Row {
                        (1..5).forEach { star ->
                            TextButton(onClick = { rating = star }) { Text(if (star <= rating) "★" else "☆") }
                        }
                    }
                    OutlinedTextField(
                        comment, { comment = it },
                        label = { Text("Comment (optional)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        feedbackDao.insert(
                            Feedback(equipmentId = item.id, userId = userId, rating = rating, comment = comment.trim())
                        )
                        Toast.makeText(context, "Thanks for your feedback!", Toast.LENGTH_SHORT).show()
                    }
                    rateItem = null
                }) { Text("Submit") }
            },
            dismissButton = { TextButton(onClick = { rateItem = null }) { Text("Cancel") } }
        )
    }
}

val sampleEquipment = listOf(
    Equipment(
        1,
        "Rossignol Skis",
        "SKI",
        "160",
        "Rossignol",
        "BEGINNER",
        45.0,
        status = "AVAILABLE",
    ),
    Equipment(
        2,
        "Burton Snowboard",
        "SNOWBOARD",
        "152",
        "Burton",
        "INTERMEDIATE",
        50.0,
        status = "AVAILABLE",

        ),
    Equipment(3, "Atomic Skis", "SKI", "170", "Atomic", "ADVANCED", 60.0, status = "RENTED")
)
