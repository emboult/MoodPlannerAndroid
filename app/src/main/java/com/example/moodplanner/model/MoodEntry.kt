// model/MoodEntry.kt
package com.example.moodplanner.model

import java.time.LocalDate
import java.util.UUID

data class MoodEntry(
    val id: String = UUID.randomUUID().toString(),
    val date: LocalDate,       // κρατάμε το LocalDate, θα το μετατρέπουμε σε string μόνο στο JSON
    val moodValue: Double,
    val description: String = ""
)
/*

Εξήγηση: Κάθε entry έχει ένα μοναδικό ID,
χρονική σφραγίδα, αριθμητική τιμή διάθεσης και προαιρετική περιγραφή.
Η date είναι computed property για ευκολία.
 */
