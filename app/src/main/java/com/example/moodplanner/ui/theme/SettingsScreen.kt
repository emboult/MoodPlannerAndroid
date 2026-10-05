package com.example.moodplanner.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.moodplanner.data.MoodRepository
import com.example.moodplanner.viewmodel.MoodViewModel
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: MoodViewModel,
    darkMode: MutableState<Boolean>,
    onToggleDarkMode: () -> Unit
) {
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showBackupDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    val repository = remember { MoodRepository(context) }
    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Back button
        Row(
            modifier = Modifier.fillMaxWidth().padding(top=20.dp, ),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(onClick = onBack, Modifier.padding(end=100.dp)) {
                TextButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Πίσω")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Ρυθμίσεις", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))


        Spacer(modifier = Modifier.height(16.dp))

        // Dark Mode toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = darkMode.value,
                onCheckedChange = { onToggleDarkMode() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))


        // Backup
        Button(
            onClick = {
                coroutineScope.launch {
                    repository.backupToLocalFolder()
                    showBackupDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) { Text("Δημιουργία Backup") }

        Spacer(modifier = Modifier.height(8.dp))

        // Restore
        Button(
            onClick = {
                coroutineScope.launch {
                    repository.restoreFromLocalFolder()
                    viewModel.refreshEntries()
                    showRestoreDialog = true
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Επαναφορά από Backup") }

        Spacer(modifier = Modifier.height(8.dp))

        // Delete all
        Button(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) { Text("Διαγραφή όλων των δεδομένων") }
    }

    // Delete dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Διαγραφή δεδομένων") },
            text = { Text("Είσαι βέβαιος; Όλες οι καταγραφές θα χαθούν οριστικά.") },
            confirmButton = {
                TextButton(onClick = {
                    repository.deleteAllEntries()
                    viewModel.refreshEntries()
                    showDeleteDialog = false
                }) { Text("Διαγραφή", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Ακύρωση") }
            }
        )
    }

    // Backup success dialog
    if (showBackupDialog) {
        AlertDialog(
            onDismissRequest = { showBackupDialog = false },
            title = { Text("Backup") },
            text = { Text("Το backup δημιουργήθηκε επιτυχώς.") },
            confirmButton = { TextButton(onClick = { showBackupDialog = false }) { Text("OK") } }
        )
    }

    // Restore success dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Επαναφορά") },
            text = { Text("Τα δεδομένα επαναφέρθηκαν.") },
            confirmButton = { TextButton(onClick = { showRestoreDialog = false }) { Text("OK") } }
        )
    }
}

/*
Παρατηρήσεις:

    Χρησιμοποιώ OutlinedTextField για την ώρα με επικύρωση μορφής HH:MM.
    Αυτό είναι προσωρινό και απλό. Στη συνέχεια μπορούμε να το αντικαταστήσουμε με Material TimePicker.
    Το MoodRepository χρειάζεται μερικές επιπλέον μεθόδους: deleteAllEntries(), backupToExternalStorage(), restoreFromExternalStorage(). Θα τις προσθέσουμε.
    Προσθέτω viewModel.refreshEntries() μετά τη διαγραφή – αλλά δεν υπάρχει ακόμα αυτή η μέθοδος στο ViewModel. Θα την κάνω public.
    Στην πραγματικότητα, το MoodViewModel έχει ήδη private fun refreshEntries(). Θα την κάνω internal ή θα προσθέσω μια δημόσια.
 */