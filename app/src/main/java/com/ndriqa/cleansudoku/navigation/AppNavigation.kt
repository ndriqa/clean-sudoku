package com.ndriqa.cleansudoku.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ndriqa.cleansudoku.core.data.SudokuBoard
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.feature.history.presentation.HistoryScreen
import com.ndriqa.cleansudoku.feature.home.presentation.HomeScreen
import com.ndriqa.cleansudoku.feature.options.presentation.OptionsScreen
import com.ndriqa.cleansudoku.feature.options.presentation.OptionsViewModel
import com.ndriqa.cleansudoku.feature.sounds.presentation.SoundsViewModel
import com.ndriqa.cleansudoku.feature.sudoku.presentation.SudokuScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val navController = rememberNavController()
//    val adsViewModel: AdsViewModel = hiltViewModel()
//    val localeViewModel: LocaleViewModel = hiltViewModel()
    val optionsViewModel: OptionsViewModel = hiltViewModel()
    val soundsViewModel: SoundsViewModel = hiltViewModel()

    val soundEnabled by optionsViewModel.soundEnabled.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> soundsViewModel.pauseBackgroundMusic()
                Lifecycle.Event.ON_RESUME -> {
                    if (soundEnabled) soundsViewModel.startBackgroundMusic()
                }
                Lifecycle.Event.ON_DESTROY -> soundsViewModel.releaseMediaPlayers()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(soundEnabled) {
        if (soundEnabled) soundsViewModel.startBackgroundMusic()
        else soundsViewModel.pauseBackgroundMusic()
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = Screens.Home.route,
    ) {
        composable(Screens.Home.route) { HomeScreen(navController) }
        composable(Screens.History.route) { HistoryScreen(navController, optionsViewModel, soundsViewModel) }
        composable(Screens.Options.route) { OptionsScreen(navController, optionsViewModel, soundsViewModel) }
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
                SudokuScreen(
                    navController = navController,
                    sudokuBoard = sudokuBoard,
                    selectedLevel = selectedLevel,
                    soundsViewModel = soundsViewModel,
                    optionsViewModel = optionsViewModel
                )
            }
        }
    }
}
