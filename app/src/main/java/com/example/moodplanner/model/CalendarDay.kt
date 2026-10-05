package com.example.moodplanner.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate?,       // null για τα κενά κελιά (padding)
    val dayOfMonth: Int,
    val averageColor: Color = Color.Gray   // προσωρινά όλες οι μέρες γκρι
)