
package com.example.moodplanner.data

import android.content.Context
import java.time.LocalDate

object AppPreferences {
    private const val PREFS_NAME = "app_prefs"
    private const val KEY_FIRST_LAUNCH = "first_launch_date"

    fun getFirstLaunchDate(context: Context): LocalDate {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dateString = prefs.getString(KEY_FIRST_LAUNCH, null)
        return if (dateString != null) LocalDate.parse(dateString) else {
            val today = LocalDate.now()
            prefs.edit().putString(KEY_FIRST_LAUNCH, today.toString()).apply()
            today
        }
    }

    //dark mode
    private const val KEY_DARK_MODE = "dark_mode"

    fun isDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)  // default: light
    }

    fun setDarkMode(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY_DARK_MODE, enabled).apply()
    }
}