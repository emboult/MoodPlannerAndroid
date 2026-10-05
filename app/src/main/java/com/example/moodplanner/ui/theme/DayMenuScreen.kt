package com.example.moodplanner.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.moodplanner.model.MoodEntry
import com.example.moodplanner.viewmodel.MoodViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


// Χρώματα για κάθε mood (ίδια με το CreateEditEntryScreen)
private val moodColors = mapOf(
    -2.0 to Color(0xFFD32F2F),  // Horrible
    -1.0 to Color(0xFFFF9800),  // Bad
    0.0  to Color(0xFFFFEB3B),  // OK
    1.0  to Color(0xFF4CAF50),  // Good
    2.0  to Color(0xFF008000)   // Amazing
)

private fun colorForMood(value: Double): Color {
    return moodColors[value] ?: Color.Gray
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayMenuScreen(
    date: LocalDate,
    viewModel: MoodViewModel,
    onBack: () -> Unit,
    onAddEntry: () -> Unit,
    onEditEntry: (String) -> Unit
) {
    val entries by remember { derivedStateOf { viewModel.getEntriesForDate(date) } }
    val hasEntries = entries.isNotEmpty()
    val averageColor = viewModel.getAverageColorForDate(date)

    var showDeleteDialog by remember { mutableStateOf(false) }
    var entryToDelete by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Back button
        Row(
            modifier = Modifier.fillMaxWidth().padding(top=20.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = onBack, Modifier.padding(end=100.dp)) {
                TextButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ημερομηνία

        Text(
            text = date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Κύκλος μέσου χρώματος
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.CenterHorizontally)
                .background(averageColor, shape = CircleShape)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Λίστα entries σαν κάρτες (ή μήνυμα αν δεν υπάρχουν)
        if (hasEntries) {
            Text(
                text = "Καταγραφές",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    EntryCard(
                        entry = entry,
                        onEdit = { onEditEntry(entry.id) },
                        onDelete = {
                            entryToDelete = entry.id
                            showDeleteDialog = true
                        }
                    )
                }
            }
        } else {
            // Κενή κατάσταση
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Δεν υπάρχουν καταγραφές για αυτή την ημέρα.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Κουμπί προσθήκης
        Button(
            onClick = onAddEntry,
            modifier = Modifier.fillMaxWidth().padding(bottom=60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Προσθήκη καταγραφής")
        }
    }

    // Διάλογος επιβεβαίωσης διαγραφής
    if (showDeleteDialog && entryToDelete != null) {
        AlertDialog(
            onDismissRequest = {
//                showDeleteDialog = false
//                entryToDelete = null
            },
            title = { Text("Διαγραφή καταγραφής") },
            text = { Text("Είσαι σίγουρος/η ότι θέλεις να διαγράψεις αυτή την καταγραφή;") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteEntry(entryToDelete!!)
//                        showDeleteDialog = false
                        entryToDelete = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Διαγραφή")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    entryToDelete = null
                }) {
                    Text("Ακύρωση")
                }
            }
        )
    }
}

@Composable
fun EntryCard(
    entry: MoodEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val moodColor = colorForMood(entry.moodValue)
    val moodName = when (entry.moodValue) {
        -2.0 -> "Horrible"
        -1.0 -> "Bad"
        0.0  -> "OK"
        1.0  -> "Good"
        2.0  -> "Amazing"
        else -> "Άγνωστο"
    }
    var lastEditClickTime by remember { mutableStateOf(0L) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = moodColor.copy(alpha = 0.08f) // πολύ απαλό φόντο
        ),
        shape = RoundedCornerShape(12.dp),

    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Μικρός κύκλος με το χρώμα του mood
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(moodColor, shape = CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Όνομα mood
                Text(
                    text = moodName,
                    style = MaterialTheme.typography.labelMedium,
                    color = moodColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                // Κουμπιά ενεργειών
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Επεξεργασία",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Διαγραφή",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            // Περιγραφή (αν υπάρχει)
            if (entry.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = entry.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}