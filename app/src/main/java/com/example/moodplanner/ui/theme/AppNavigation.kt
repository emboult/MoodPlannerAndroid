// AppNavigation.kt
package com.example.moodplanner.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.moodplanner.viewmodel.MoodViewModel
import java.time.LocalDate


@Composable
fun AppNavigation(
    navController: NavHostController,
    darkMode: MutableState<Boolean>,
    onToggleDarkMode: () -> Unit
) {
    val moodViewModel: MoodViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.CALENDAR
    ) {
        composable(NavRoutes.CALENDAR) {
            CalendarScreen(
                viewModel = moodViewModel,
                onDayClick = { date ->
                    navController.navigate("day_menu/${date.toString()}")
                },
                onSettingsClick = {
                    navController.navigate(NavRoutes.SETTINGS)
                }
            )
        }
        composable(
            route = NavRoutes.DAY_MENU,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: return@composable
            val date = LocalDate.parse(dateString)
            DayMenuScreen(
                date = date,
                viewModel = moodViewModel,
                onBack = { navController.popBackStack() },
                onAddEntry = {
                    navController.navigate("create_edit_entry/${date.toString()}/new")
                },
                onEditEntry = { entryId ->
                    navController.navigate("create_edit_entry/${date.toString()}/${entryId}")
                }
            )
        }
        composable(
            route = NavRoutes.CREATE_EDIT_ENTRY,
            arguments = listOf(
                navArgument("date") { type = NavType.StringType },
                navArgument("entryId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: return@composable
            val entryId = backStackEntry.arguments?.getString("entryId") ?: return@composable
            val date = LocalDate.parse(dateString)
            val existingEntry = if (entryId != "new") {
                moodViewModel.getEntriesForDate(date).find { it.id == entryId }
            } else null

            CreateEditEntryScreen(
                date = date,
                viewModel = moodViewModel,
                existingEntry = existingEntry,
                onCancel = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
        composable(NavRoutes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                viewModel = moodViewModel,
                darkMode = darkMode,
                onToggleDarkMode = onToggleDarkMode
            )
        }
    }
}