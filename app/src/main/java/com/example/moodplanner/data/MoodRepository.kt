// data/MoodRepository.kt
package com.example.moodplanner.data

import android.content.Context
import com.example.moodplanner.model.MoodEntry
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDate

class MoodRepository(private val context: Context) {

    private val filename = "mood_entries.json"
    private val file: File
        get() = File(context.filesDir, filename)

    fun getAllEntries(): List<MoodEntry> {
        if (!file.exists()) return emptyList()
        return try {
            val content = file.readText()
            val jsonArray = JSONArray(content)
            val entries = mutableListOf<MoodEntry>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val entry = MoodEntry(
                    id = obj.getString("id"),
                    date = LocalDate.parse(obj.getString("date")),
                    moodValue = obj.getDouble("moodValue"),
                    description = obj.optString("description", "")
                )
                entries.add(entry)
            }
            entries
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun saveAllEntries(entries: List<MoodEntry>) {
        val jsonArray = JSONArray()
        entries.forEach { entry ->
            val obj = JSONObject().apply {
                put("id", entry.id)
                put("date", entry.date.toString())       // LocalDate -> String
                put("moodValue", entry.moodValue)
                put("description", entry.description)
            }
            jsonArray.put(obj)
        }
        file.writeText(jsonArray.toString(4)) // pretty print με αλλαγές γραμμής
    }

    fun getEntriesForDate(date: LocalDate): List<MoodEntry> {
        return getAllEntries().filter { it.date == date }
    }

    fun addEntry(entry: MoodEntry) {
        val current = getAllEntries().toMutableList()
        current.add(entry)
        saveAllEntries(current)
    }

    fun updateEntry(updatedEntry: MoodEntry) {
        val current = getAllEntries().toMutableList()
        val index = current.indexOfFirst { it.id == updatedEntry.id }
        if (index != -1) {
            current[index] = updatedEntry
            saveAllEntries(current)
        }
    }

    fun deleteEntry(entryId: String) {
        val current = getAllEntries().toMutableList()
        current.removeAll { it.id == entryId }
        saveAllEntries(current)
    }

/*
Διαβάζουμε πάντα από το αρχείο πριν από κάθε λειτουργία, και ξαναγράφουμε ολόκληρη τη λίστα.
Για μεγάλο όγκο δεδομένων θα μπορούσαμε να κάνουμε append, αλλά για mood entries είναι υπέρ-αρκετό.
 */

    fun deleteAllEntries() {
        file.delete()
    }

    fun backupToLocalFolder() {
        if (!file.exists()) return
        val backupDir = File(context.filesDir, "backups")
        if (!backupDir.exists()) backupDir.mkdirs()
        val backupFile = File(backupDir, "mood_backup.json")
        file.copyTo(backupFile, overwrite = true)
    }

    fun restoreFromLocalFolder() {
        val backupFile = File(context.filesDir, "backups/mood_backup.json")
        if (backupFile.exists()) {
            backupFile.copyTo(file, overwrite = true)
        }
    }
}