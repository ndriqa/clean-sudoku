package com.ndriqa.cleansudoku.feature.home.presentation

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import com.ndriqa.cleansudoku.navigation.ndriqaDonate
import com.ndriqa.cleansudoku.navigation.ndriqaOtherApps
import com.ndriqa.cleansudoku.ui.components.SplitScreen
import com.ndriqa.cleansudoku.ui.data.UiState
import com.ndriqa.cleansudoku.ui.theme.CardSize
import com.ndriqa.cleansudoku.ui.theme.CleanSudokuTheme
import com.ndriqa.cleansudoku.ui.theme.HomeButtonDefault
import com.ndriqa.cleansudoku.ui.theme.HomeButtonElevation
import com.ndriqa.cleansudoku.ui.theme.PaddingDefault
import com.ndriqa.cleansudoku.ui.theme.PaddingHalf
import com.ndriqa.cleansudoku.ui.theme.PaddingMini

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
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
            onNavigateToDonate = { ndriqaDonate(context) },
            onNavigateToOptions = { navController.navigate(Screens.Options.route) },
            onNavigateToHistory = { navController.navigate(Screens.History.route) }
        ) },
        primaryContentRatio = 3F,
        secondaryContentRatio = if (isLandscape) 3F else 4F
    )
}

@Composable
private fun HeaderContent() {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingMini, alignment = Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_logo_outline),
            contentDescription = null,
            modifier = Modifier.size(CardSize),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
        )
        Text(
            text = stringResource(R.string.app_name),
            fontSize = if (isLandscape) 24.sp else 30.sp
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
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = if (isLandscape) PaddingMini else PaddingHalf,
            alignment = Alignment.CenterVertically
        )
    ) {

        Button(
            onClick = onGenerateSudoku,
            enabled = sudokuState !is UiState.Loading,
            modifier = Modifier.defaultMinSize(minWidth = HomeButtonDefault),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = HomeButtonElevation
            )
        ) {
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
        HomeButton(onClick = onNavigateToHistory, titleResId = R.string.history)
        HomeButton(onClick = onNavigateToOptions, titleResId = R.string.options)
        HomeButton(onClick = onNavigateToDonate, titleResId = R.string.donate)
        HomeButton(onClick = onNavigateToOtherApps, titleResId = R.string.other_apps)
    }
}

@Composable
private fun HomeButton(
    onClick: () -> Unit,
    @StringRes titleResId: Int,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.defaultMinSize(minWidth = HomeButtonDefault)
    ) { Text(stringResource(titleResId)) }
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
