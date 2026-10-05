package com.example.moodplanner.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.moodplanner.data.MoodRepository
import com.example.moodplanner.model.MoodEntry
import java.time.LocalDate

class MoodViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MoodRepository(application.applicationContext)

    var entries: List<MoodEntry> by mutableStateOf(emptyList())
        private set

    init {
        refreshEntries()
    }

    fun refreshEntries() {
        entries = repository.getAllEntries()
    }

    /*
    Το repository.getAllEntries():

    Ελέγχει αν υπάρχει το αρχείο mood_entries.json.

    Αν υπάρχει, διαβάζει το περιεχόμενο (String).

    Το μετατρέπει σε JSONArray και φτιάχνει μία λίστα από MoodEntry.

    Επιστρέφει τη λίστα
     */

    fun getEntriesForDate(date: LocalDate): List<MoodEntry> {
        return entries.filter { it.date == date }
    }

    fun getAverageColorForDate(date: LocalDate): androidx.compose.ui.graphics.Color {
        // Χρησιμοποίησε μια τοπική αναφορά για να μην διαβάζεται το state πολλές φορές
        val currentEntries = entries
        val dayEntries = currentEntries.filter { it.date == date }

        if (dayEntries.isEmpty()) return androidx.compose.ui.graphics.Color(0xFF86758C)

        val averageValue = dayEntries.map { it.moodValue }.average()
        return when {
            averageValue < -1.5 -> androidx.compose.ui.graphics.Color(0xFFD32F2F)
            averageValue < -0.5 -> androidx.compose.ui.graphics.Color(0xFFFF9800)
            averageValue < 0.5  -> androidx.compose.ui.graphics.Color(0xFFFFEB3B)
            averageValue < 1.5  -> androidx.compose.ui.graphics.Color(0xFF4CAF50)
            else                -> androidx.compose.ui.graphics.Color(0xFF008000)
        }
    }

    fun addEntry(moodValue: Double, description: String, date: LocalDate) {
        val entry = MoodEntry(
            moodValue = moodValue,
            description = description,
            date = date               // ήδη LocalDate, δε χρειάζεται atStartOfDay()
        )
        repository.addEntry(entry)
        refreshEntries()
    }

    fun updateEntry(entryId: String, newMoodValue: Double, newDescription: String) {
        val existing = entries.find { it.id == entryId } ?: return
        val updated = existing.copy(
            moodValue = newMoodValue,
            description = newDescription
        )
        repository.updateEntry(updated)
        refreshEntries()
    }

    fun deleteEntry(entryId: String) {
        repository.deleteEntry(entryId)
        refreshEntries()
    }


}

/*
Εξήγηση: Το ViewModel κρατάει όλη την κατάσταση και εκθέτει συναρτήσεις που θα
καλούν τα composables. Χρησιμοποιεί mutableStateOf για να ειδοποιεί την UI αυτόματα όταν αλλάζουν τα δεδομένα

Update: Αλλάζουμε από ViewModel σε AndroidViewModel για να έχουμε το Context.
 */