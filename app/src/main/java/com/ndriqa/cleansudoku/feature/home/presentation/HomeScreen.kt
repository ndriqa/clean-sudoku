package com.ndriqa.cleansudoku.feature.home.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ndriqa.cleansudoku.FullPreviews
import com.ndriqa.cleansudoku.R
import com.ndriqa.cleansudoku.core.data.SudokuBoard
import com.ndriqa.cleansudoku.core.domain.preferences.DataStoreManager
import com.ndriqa.cleansudoku.navigation.Screens
import com.ndriqa.cleansudoku.navigation.ndriqaOtherApps
import com.ndriqa.cleansudoku.ui.components.SplitScreen
import com.ndriqa.cleansudoku.ui.data.UiState
import com.ndriqa.cleansudoku.ui.theme.CleanSudokuTheme
import com.ndriqa.cleansudoku.ui.theme.PaddingDefault

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val selectedLevel by viewModel.preferredDifficulty.collectAsState()
    val sudokuState by viewModel.sudoku.collectAsState()
    val generatedSudoku by remember { derivedStateOf {
        sudokuState as? UiState.Success
    } }

    LaunchedEffect(generatedSudoku) {
        viewModel.resetGeneratedSudoku()
        generatedSudoku?.let { sudoku ->
            navController.currentBackStackEntry?.savedStateHandle?.apply {
                set("sudokuBoard", sudoku.data)
                set("selectedLevel", selectedLevel)
            }
            navController.navigate(Screens.Sudoku.route)
        }
    }

    SplitScreen(
        primaryContent = { HeaderContent() },
        secondaryContent = { HomeContent(
            sudokuState = sudokuState,
            onGenerateSudoku = viewModel::tryGenerateSudoku,
            onNavigateToOtherApps = { ndriqaOtherApps(context) },
            onNavigateToDonate = {  }, // TODO
            onNavigateToOptions = {  }, // TODO
            onNavigateToHistory = { navController.navigate(Screens.History.route) }
        ) }
    )
}

@Composable
private fun HeaderContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontSize = 34.sp
        )
        Text(
            text = stringResource(R.string.by_ndriqa),
        )
    }
}

@Composable
private fun HomeContent(
    sudokuState: UiState<SudokuBoard>,
    onGenerateSudoku: () -> Unit,
    onNavigateToOtherApps: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToOptions: () -> Unit,
    onNavigateToDonate: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingDefault, alignment = Alignment.CenterVertically)
    ) {

        Button(onClick = onGenerateSudoku, enabled = sudokuState !is UiState.Loading) {
            AnimatedContent(sudokuState, label = "play button state") {
                when(it) {
                    is UiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                    else -> Text(stringResource(R.string.play), fontWeight = FontWeight.Bold)
                }
            }
        }
        Button(onClick = onNavigateToHistory) { Text(stringResource(R.string.history)) }
        Button(onClick = onNavigateToOptions) { Text(stringResource(R.string.options)) }
        Button(onClick = onNavigateToDonate) { Text(stringResource(R.string.donate)) }
        Button(onClick = onNavigateToOtherApps) { Text(stringResource(R.string.other_apps)) }
    }
}

@FullPreviews
@Composable
private fun HomeScreenPreview() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val viewModel = HomeViewModel(
        dataStoreManager = DataStoreManager(context)
    )

    CleanSudokuTheme {
        HomeScreen(navController, viewModel)
    }
}
