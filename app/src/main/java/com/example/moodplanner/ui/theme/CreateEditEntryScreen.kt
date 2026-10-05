package com.example.moodplanner.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodplanner.model.MoodEntry
import com.example.moodplanner.viewmodel.MoodViewModel
import java.time.LocalDate

// Τα 5 moods και τα χρώματά τους (Material3)
data class MoodOption(
    val name: String,
    val value: Double,
    val color: Color
)

val moodOptions = listOf(
    MoodOption("Horrible", -2.0, Color(0xFFD32F2F)), // Κόκκινο
    MoodOption("Bad", -1.0, Color(0xFFFF9800)),      // Πορτοκαλί
    MoodOption("OK", 0.0, Color(0xFFFFEB3B)),        // Κίτρινο
    MoodOption("Good", 1.0, Color(0xFF4CAF50)),      // Πράσινο
    MoodOption("Amazing", 2.0, Color(0xFF008000))    // Σκούρο Πράσινο
)

@Composable
fun CreateEditEntryScreen(
    date: LocalDate,
    viewModel: MoodViewModel,
    existingEntry: MoodEntry? = null, // null για δημιουργία, αλλιώς για επεξεργασία
    onCancel: () -> Unit,
    onSaved: () -> Unit
) {
    val isEditing = existingEntry != null
    var selectedMood by remember { mutableStateOf(existingEntry?.moodValue ?: 0.0) }
    var description by remember { mutableStateOf(existingEntry?.description ?: "") }

    // Cooldown για το κουμπί αποθήκευσης
    var lastClickTime by remember { mutableStateOf(0L) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp)
            .scale(0.9F),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Τίτλος
        Text(
            text = if (isEditing) "Επεξεργασία Καταγραφής" else "Νέα Καταγραφή",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ημερομηνία
        Text(
            text = date.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Επιλογή Mood - 5 κύκλοι
        Text(
            text = "Πώς νιώθεις;",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            moodOptions.forEach { option ->
                val isSelected = selectedMood == option.value
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(option.color.copy(alpha = if (isSelected) 1f else 0.25f))
                        .border(
                            width = if (isSelected) 3.dp else 0.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape
                        )
                        .clickable { selectedMood = option.value },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.name.substring(0, 2),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Περιγραφή
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Περιγραφή (προαιρετική)") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Κουμπιά Αποθήκευσης / Ακύρωσης
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Cancel
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier.weight(1f)
            ) {
                Text("Ακύρωση")
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Save
            Button(
                onClick = {
                    val now = System.currentTimeMillis()
                    if (now - lastClickTime > 500) { // 500ms cooldown
                        lastClickTime = now
                        if (isEditing) {
                            viewModel.updateEntry(existingEntry!!.id, selectedMood, description)
                        } else {
                            viewModel.addEntry(selectedMood, description, date)
                        }
                        onSaved()
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = true
            ) {
                Text(if (isEditing) "Ενημέρωση" else "Καταγραφή")
            }
        }
    }
}

/*
Τα moods αποθηκεύονται σε μια λίστα moodOptions με το όνομα, την τιμή και το χρώμα τους.

Όταν ο χρήστης επιλέγει ένα mood, το αντίστοιχο κουμπί αποκτά πλήρη αδιαφάνεια και περίγραμμα, ενώ τα υπόλοιπα γίνονται ημιδιάφανα.

Το OutlinedTextField επιτρέπει την καταχώρηση προαιρετικής περιγραφής.

Αν το existingEntry είναι null, είμαστε σε κατάσταση δημιουργίας και το κουμπί γράφει "Καταγραφή". Αλλιώς, είμαστε σε επεξεργασία και καλείται η updateEntry
 */