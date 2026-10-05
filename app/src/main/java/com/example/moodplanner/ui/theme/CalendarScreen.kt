package com.example.moodplanner.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.moodplanner.data.AppPreferences
import com.example.moodplanner.model.CalendarDay
import com.example.moodplanner.model.getDaysInMonth
import com.example.moodplanner.viewmodel.MoodViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight

@Composable
fun CalendarScreen(
    viewModel: MoodViewModel,
    onDayClick: (LocalDate) -> Unit,
    onSettingsClick: () -> Unit
) {

    val context = LocalContext.current
    val today = LocalDate.now()
    val firstLaunchDate = remember { AppPreferences.getFirstLaunchDate(context) }

    // Λίστα (year, month) από τον σημερινό μήνα μέχρι τον πρώτο μήνα λειτουργίας
    val months = remember(firstLaunchDate, today) {
        generateMonthList(firstLaunchDate, today)

    }

    Box(modifier = Modifier.fillMaxSize().padding(top=30.dp)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(top=80.dp),

        ) {
            items(months.size) { index ->
                val (year, month) = months[index]
                MonthView(
                    year = year,
                    month = month,
                    viewModel = viewModel,
                    onDayClick = onDayClick,
                    isCurrentMonth = (year == today.year && month == today.monthValue)
                )
            }

        }
        Text("Welcome", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(vertical = 40.dp, horizontal = 20.dp))
        // Settings button (πάνω δεξιά, σταθερό)
        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp, top=35.dp)
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Ρυθμίσεις",
                tint = MaterialTheme.colorScheme.primary
            )

        }

    }
}


/**
 * Επιστρέφει μια λίστα ζευγών (year, month) από τον σημερινό μήνα
 * μέχρι τον monthFrom (παλαιότερο) σε αντίστροφη χρονολογική σειρά.
 */
private fun generateMonthList(firstLaunch: LocalDate, today: LocalDate): List<Pair<Int, Int>> {
    val months = mutableListOf<Pair<Int, Int>>()

    // Ξεκινάμε από τον σημερινό μήνα
    var current = YearMonth.from(today)
    val stop = YearMonth.from(firstLaunch)

    // Μετρητής ασφαλείας: μέγιστο 1200 μήνες (100 χρόνια)
    var safetyCounter = 0

    while (current.isAfter(stop) || current == stop) {
        months.add(current.year to current.monthValue)

        // Πάμε στον προηγούμενο μήνα
        current = current.minusMonths(1)

        safetyCounter++
        if (safetyCounter > 1200) {
            // Κάτι πήγε στραβά, σταμάτα για ασφάλεια
            break
        }
    }

    return months
}

@Composable
fun MonthView(
    year: Int,
    month: Int,
    viewModel: MoodViewModel,
    onDayClick: (LocalDate) -> Unit,
    isCurrentMonth: Boolean
) {
    val monthName = YearMonth.of(year, month).month
        .getDisplayName(java.time.format.TextStyle.FULL, Locale.getDefault())


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrentMonth)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "$monthName $year",
                style = MaterialTheme.typography.titleMedium,
                color = if (isCurrentMonth) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Επικεφαλίδες ημερών με χρώμα Σαββατοκύριακου
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf(
                    "Δε" to false, "Τρ" to false, "Τε" to false,
                    "Πε" to false, "Πα" to false, "Σα" to true, "Κυ" to true
                ).forEach { (day, isWeekend) ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isWeekend)
                            MaterialTheme.colorScheme.error  // κόκκινο για ΣΚ
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isWeekend) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Πλέγμα ημερών
            val days = getDaysInMonth(year, month)
            Column(modifier = Modifier.fillMaxWidth()) {
                for (rowStart in days.indices step 7) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (i in 0 until 7) {
                            val day = days.getOrNull(rowStart + i) ?: CalendarDay(null, 0)
                            val color = if (day.date != null) viewModel.getAverageColorForDate(day.date) else Color.Transparent

                            DayCell(
                                day = day.copy(averageColor = color),
                                modifier = Modifier.weight(1f),
                                onClick = onDayClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: CalendarDay,
    modifier: Modifier = Modifier,
    onClick: (LocalDate) -> Unit = {}
) {
    val today = LocalDate.now()
    val isToday = day.date == today
    val isClickable = day.date != null && !day.date.isAfter(today)
    var lastClickTime by remember { mutableStateOf(0L) }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(day.averageColor, shape = CircleShape)
            .then(
                if (isToday) Modifier.border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) else Modifier
            )
            .then(
                if (isClickable) Modifier.clickable {
                    val now = System.currentTimeMillis()
                    if (now - lastClickTime > 500) { // 500ms cooldown
                        lastClickTime = now
                        onClick(day.date)
                    }
                }
                else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (day.date != null) {
            Text(
                text = day.dayOfMonth.toString(),
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}