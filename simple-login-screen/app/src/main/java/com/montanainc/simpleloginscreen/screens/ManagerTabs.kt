@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.montanainc.simpleloginscreen.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.montanainc.simpleloginscreen.FeedbackDetails
import com.montanainc.simpleloginscreen.RentalSchedule
import com.montanainc.simpleloginscreen.RevenueRow
import com.montanainc.simpleloginscreen.entities.AppBanner
import com.montanainc.simpleloginscreen.entities.Discount
import com.montanainc.simpleloginscreen.entities.Equipment
import com.montanainc.simpleloginscreen.entities.WaitlistEntry
import com.montanainc.simpleloginscreen.money
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

val CHECK_IN_STATUSES = setOf("CONFIRMED", "RESERVED")

fun formatDate(ms: Long): String =
    SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(ms))


@Composable
fun ScheduleTab(rentals: List<RentalSchedule>) {
    val checkIns = rentals.filter { it.status in CHECK_IN_STATUSES }.sortedBy { it.startDate }
    val checkOuts = rentals.filter { it.status == "CHECKED_OUT" || it.status == "OVERDUE" }.sortedBy { it.endDate }

    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        item { Text("Expected check-ins (pickups)", style = MaterialTheme.typography.titleMedium) }
        item { ScheduleRow("Pickup", "Booking", "Customer", "Item", bold = true) }
        if (checkIns.isEmpty()) item { Text("None scheduled.") }
        items(checkIns, key = { "in-${it.id}" }) { r ->
            ScheduleRow(formatDate(r.startDate), r.bookingId, r.customer, r.itemName)
        }
        item { Spacer(Modifier.height(20.dp)) }
        item { Text("Expected check-outs (returns)", style = MaterialTheme.typography.titleMedium) }
        item { ScheduleRow("Due back", "Booking", "Customer", "Item", bold = true) }
        if (checkOuts.isEmpty()) item { Text("None scheduled.") }
        items(checkOuts, key = { "out-${it.id}" }) { r ->
            ScheduleRow(formatDate(r.endDate), r.bookingId, r.customer, r.itemName)
        }
    }
}

@Composable
private fun ScheduleRow(date: String, booking: String, customer: String, item: String, bold: Boolean = false) {
    val weight = if (bold) FontWeight.Bold else FontWeight.Normal
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(date, modifier = Modifier.weight(1.2f), fontWeight = weight, fontSize = 12.sp)
        Text(booking, modifier = Modifier.weight(1f), fontWeight = weight, fontSize = 12.sp)
        Text(customer, modifier = Modifier.weight(1f), fontWeight = weight, fontSize = 12.sp)
        Text(item, modifier = Modifier.weight(1.2f), fontWeight = weight, fontSize = 12.sp)
    }
}


fun dailyRevenue(rows: List<RevenueRow>, days: Int = 14): List<Pair<String, Double>> {
    val labelFmt = SimpleDateFormat("d", Locale.getDefault())
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, -(days - 1))
    }
    val result = mutableListOf<Pair<String, Double>>()
    repeat(days) {
        val from = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val to = cal.timeInMillis
        val sum = rows.filter { it.startDate in from until to }.sumOf { it.amount }
        result += labelFmt.format(Date(from)) to sum
    }
    return result
}

@Composable
fun RevenueTab(rows: List<RevenueRow>) {
    val daily = remember(rows) { dailyRevenue(rows) }
    val inRange = daily.sumOf { it.second }
    val allTime = rows.sumOf { it.amount }

    Column(Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Revenue, last 14 days", style = MaterialTheme.typography.titleMedium)
        Text("Last 14 days: ${money(inRange)}  ·  All time: ${money(allTime)}")
        Spacer(Modifier.height(12.dp))
        RevenueBarChart(daily)
        Spacer(Modifier.height(8.dp))
        Text(
            "Bars show the day of the month. Revenue counts once equipment is checked out and includes late fees. Cancelled bookings are excluded.",
            fontSize = 12.sp
        )
    }
}

@Composable
fun RevenueBarChart(data: List<Pair<String, Double>>) {
    val maxValue = (data.maxOfOrNull { it.second } ?: 0.0).coerceAtLeast(1.0)
    val barColor = MaterialTheme.colorScheme.primary

    Column(Modifier.fillMaxWidth()) {
        Text("Peak: ${money(maxValue)}", fontSize = 11.sp)
        Canvas(Modifier.fillMaxWidth().height(180.dp)) {
            val slot = size.width / data.size.coerceAtLeast(1)
            data.forEachIndexed { i, point ->
                val barHeight = (point.second / maxValue).toFloat() * size.height
                drawRect(
                    color = barColor,
                    topLeft = Offset(i * slot + slot * 0.15f, size.height - barHeight),
                    size = Size(slot * 0.7f, barHeight)
                )
            }
            drawLine(Color.Gray, Offset(0f, size.height), Offset(size.width, size.height), strokeWidth = 2f)
        }
        Row(Modifier.fillMaxWidth()) {
            data.forEach { point ->
                Text(point.first, modifier = Modifier.weight(1f), fontSize = 9.sp, textAlign = TextAlign.Center)
            }
        }
    }
}


@Composable
fun FeedbackTab(feedback: List<FeedbackDetails>) {
    if (feedback.isEmpty()) {
        Text("No feedback yet. Customers can leave it from My Rentals after returning gear.", Modifier.padding(16.dp))
        return
    }
    val average = feedback.map { it.rating }.average()
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        item { Text("Average rating: ${"%.1f".format(average)} / 5  (${feedback.size} reviews)", style = MaterialTheme.typography.titleMedium) }
        items(feedback, key = { it.id }) { f ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(f.itemName ?: "Equipment ${f.equipmentId}", style = MaterialTheme.typography.titleMedium)
                    val stars = f.rating.coerceIn(0, 5)
                    Text("★".repeat(stars) + "☆".repeat(5 - stars))
                    if (f.comment.isNotBlank()) Text(f.comment)
                    Text(formatDate(f.createdAt), fontSize = 11.sp)
                }
            }
        }
    }
}


@Composable
fun WaitlistTab(waiting: List<WaitlistEntry>, inventory: List<Equipment>) {
    if (waiting.isEmpty()) {
        Text("Nobody is waiting. Customers can join the waitlist on any rented item.", Modifier.padding(16.dp))
        return
    }
    val grouped = waiting.groupBy { it.equipmentId }
    LazyColumn(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        grouped.forEach { (equipmentId, queue) ->
            val name = inventory.find { it.id == equipmentId }?.name ?: "Equipment $equipmentId"
            item(key = "wl-$equipmentId") {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("$name (${queue.size} waiting)", style = MaterialTheme.typography.titleMedium)
                        queue.forEachIndexed { index, entry ->
                            Text("${index + 1}. ${entry.email} · joined ${formatDate(entry.createdAt)}")
                        }
                        Text("The first person is emailed automatically when this item is returned.", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun PromosTab(
    discounts: List<Discount>,
    banner: AppBanner?,
    onAddDiscount: (String, Double) -> Unit,
    onToggleDiscount: (Discount) -> Unit,
    onSaveBanner: (String, Boolean) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var bannerText by remember(banner) { mutableStateOf(banner?.message ?: "") }
    var bannerOn by remember(banner) { mutableStateOf(banner?.isActive ?: false) }

    Column(
        Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Homepage banner", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            bannerText, { bannerText = it },
            label = { Text("Banner message") }, modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(checked = bannerOn, onCheckedChange = { bannerOn = it })
            Spacer(Modifier.width(8.dp))
            Text("Show banner to customers")
        }
        Button(
            onClick = { onSaveBanner(bannerText.trim(), bannerOn) },
            enabled = bannerText.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Banner") }

        Spacer(Modifier.height(16.dp))
        Text("Discount codes (fixed amount off)", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                code, { code = it },
                label = { Text("Code") }, singleLine = true, modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                amount, { amount = it },
                label = { Text("\$ off") }, singleLine = true, modifier = Modifier.weight(1f)
            )
        }
        Button(
            onClick = {
                onAddDiscount(code.trim().uppercase(), amount.toDouble())
                code = ""; amount = ""
            },
            enabled = code.isNotBlank() && (amount.toDoubleOrNull() ?: 0.0) > 0.0,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Add Discount Code") }

        discounts.forEach { d ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${d.code} · ${money(d.amount)} off")
                Switch(checked = d.active, onCheckedChange = { onToggleDiscount(d) })
            }
        }
    }
}


private const val TRAIL_MAP_HTML = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
<style>html, body, #map { height: 100%; margin: 0; }</style>
</head>
<body>
<div id="map"></div>
<script>
  var map = L.map('map').setView([44.5016, -80.3115], 14);
  L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map);
  L.marker([44.5016, -80.3115]).addTo(map).bindPopup('Blue Mountain');
</script>
</body>
</html>
"""

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TrailMapTab() {
    val context = androidx.compose.ui.platform.LocalContext.current

    Column(Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadDataWithBaseURL("https://localhost/", TRAIL_MAP_HTML, "text/html", "UTF-8", null)
                }
            }
        )
        Button(
            onClick = {
                context.startActivity(
                    android.content.Intent(
                        android.content.Intent.ACTION_VIEW,
                        android.net.Uri.parse("https://www.openstreetmap.org/#map=14/44.5016/-80.3115")
                    )
                )
            },
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        ) { Text("Open trail map in browser") }
    }
}
