package com.ndriqa.cleansudoku.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ndriqa.cleansudoku.core.data.SudokuBoard
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.feature.history.presentation.HistoryScreen
import com.ndriqa.cleansudoku.feature.home.presentation.HomeScreen
import com.ndriqa.cleansudoku.feature.sudoku.presentation.SudokuScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    val navController = rememberNavController()
//    val adsViewModel: AdsViewModel = hiltViewModel()
//    val localeViewModel: LocaleViewModel = hiltViewModel()
    val context = LocalContext.current

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Home.route,
    ) {
        composable(Screens.Home.route) { HomeScreen(navController) }
        composable(Screens.History.route) { HistoryScreen(navController) }
        composable(Screens.Sudoku.route) { backStackEntry ->
            val stateHandle = navController
                .previousBackStackEntry
                ?.savedStateHandle
            val sudokuBoard: SudokuBoard? = stateHandle?.get<SudokuBoard>("sudokuBoard")
            val selectedLevel: Level? = stateHandle?.get<Level>("selectedLevel")

            if (sudokuBoard == null || selectedLevel == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack(Screens.Home.route, inclusive = false)
                }
            } else {
                SudokuScreen(navController, sudokuBoard, selectedLevel)
            }
        }
    }
}
