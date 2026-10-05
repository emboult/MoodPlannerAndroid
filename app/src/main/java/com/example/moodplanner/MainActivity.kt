package com.example.moodplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.moodplanner.data.AppPreferences
import com.example.moodplanner.ui.theme.AppNavigation
import com.example.moodplanner.ui.theme.MoodPlannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val darkMode = remember { mutableStateOf(AppPreferences.isDarkMode(this)) }

            MoodPlannerTheme(darkTheme = darkMode.value) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        darkMode = darkMode,
                        onToggleDarkMode = {
                            darkMode.value = !darkMode.value
                            AppPreferences.setDarkMode(this, darkMode.value)
                        }
                    )
                }
            }
        }
    }
}