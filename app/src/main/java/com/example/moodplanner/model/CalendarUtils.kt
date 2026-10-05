package com.example.moodplanner.model

import java.time.DayOfWeek
import java.time.YearMonth


fun getDaysInMonth(year: Int, month: Int): List<CalendarDay> {
    val yearMonth = YearMonth.of(year, month)
    val firstOfMonth = yearMonth.atDay(1)

    // Πόσα κενά κελιά πριν την 1η (ξεκινώντας εβδομάδα από Δευτέρα)
    val startDayOfWeek = firstOfMonth.dayOfWeek
    val leadingEmptyDays = (startDayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7

    val totalDays = leadingEmptyDays + yearMonth.lengthOfMonth()
    val totalCells = ((totalDays + 6) / 7) * 7   // στρογγυλοποίηση στο πολλαπλάσιο του 7
    val trailingEmptyDays = totalCells - totalDays

    val days = mutableListOf<CalendarDay>()
    repeat(leadingEmptyDays) {
        days.add(CalendarDay(date = null, dayOfMonth = 0))
    }
    for (day in 1..yearMonth.lengthOfMonth()) {
        days.add(CalendarDay(date = yearMonth.atDay(day), dayOfMonth = day))
    }
    repeat(trailingEmptyDays) {
        days.add(CalendarDay(date = null, dayOfMonth = 0))
    }
    return days
}