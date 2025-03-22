package com.ndriqa.cleansudoku.feature.history.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ndriqa.cleansudoku.R
import com.ndriqa.cleansudoku.core.util.extensions.formatDateTime
import com.ndriqa.cleansudoku.core.util.extensions.getMaterialIcon
import com.ndriqa.cleansudoku.core.util.extensions.toFormattedTime
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.feature.options.presentation.OptionsViewModel
import com.ndriqa.cleansudoku.feature.sounds.presentation.SoundsViewModel
import com.ndriqa.cleansudoku.ui.components.SplitScreen
import com.ndriqa.cleansudoku.ui.components.TopBarUi
import com.ndriqa.cleansudoku.ui.theme.CardSizeBig
import com.ndriqa.cleansudoku.ui.theme.PaddingDefault
import com.ndriqa.cleansudoku.ui.theme.PaddingHalf
import com.ndriqa.cleansudoku.ui.theme.PaddingMini
import com.ndriqa.cleansudoku.ui.theme.PaddingNano
import com.ndriqa.cleansudoku.ui.theme.TopBarSize

@Composable
fun HistoryScreen(
    navController: NavController,
    optionsViewModel: OptionsViewModel = hiltViewModel(),
    soundsViewModel: SoundsViewModel = hiltViewModel(),
    viewModel: HistoryViewModel = hiltViewModel(),
) {
    val selectedLevelState by viewModel.selectedLevelState.collectAsState()
    val selectedLevel by remember { derivedStateOf {
        val readyState = (selectedLevelState as? HistoryViewModel.LevelSelectionState.Ready)
        readyState?.level ?: Level.EASY
    } }
    val selectedLevelStats by viewModel.selectedStats.collectAsState()
    val soundEnabled by optionsViewModel.soundEnabled.collectAsState()

    fun updateSelectedLevel(level: Level) {
        if (soundEnabled) soundsViewModel.switch()
        viewModel.updateSelectedLevel(level)
    }

    Scaffold(
        topBar = { TopBarUi(onBackPress = navController::navigateUp) },
        containerColor = Color.Transparent
    ) { paddingValues ->

        SplitScreen(
            modifier = Modifier.padding(paddingValues),
            primaryContent = { BasicStatsUi(
                viewModel = viewModel
            ) },
            secondaryContent = { LevelsStatsUi(
                selectedLevel = selectedLevel,
                selectedLevelStats = selectedLevelStats,
                onLevelSelectionChange = ::updateSelectedLevel
            ) }
        )
    }
}

@Composable
fun LevelsStatsUi(
    selectedLevel: Level,
    selectedLevelStats: Map<String, String>,
    onLevelSelectionChange: (Level) -> Unit
) {
    val tableShape = RoundedCornerShape(PaddingDefault)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .defaultMinSize(minHeight = CardSizeBig)
            .clip(tableShape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = tableShape
            )
            .verticalScroll(rememberScrollState()),
    ) {
        LevelSelectorUi(
            currentSelectedLevel = selectedLevel,
            onLevelClicked = onLevelSelectionChange
        )

        Spacer(modifier = Modifier.height(PaddingHalf))

        if (selectedLevelStats.isNotEmpty()) {
            selectedLevelStats.forEach { (key, value) ->
                TableRow(key, value)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) { Text(text = stringResource(R.string.label_no_games_yet)) }
        }

        Spacer(modifier = Modifier.height(PaddingHalf))
    }
}

@Composable
private fun LevelSelectorUi(
    currentSelectedLevel: Level,
    onLevelClicked: (Level) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TopBarSize)
    ) {
        Level.entries.map { LevelItemUi(
            level = it,
            selected = currentSelectedLevel == it,
            onLevelClicked = onLevelClicked
        ) }
    }
}

@Composable
private fun RowScope.LevelItemUi(
    level: Level,
    selected: Boolean,
    onLevelClicked: (Level) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedColor =
        if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onPrimary

    val onSelectedColor =
        if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .weight(1F)
            .background(color = selectedColor)
            .clickable(onClick = { onLevelClicked(level) })
            .padding(PaddingNano)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PaddingNano, alignment = Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = level.getMaterialIcon(),
                contentDescription = stringResource(level.titleResId),
                tint = onSelectedColor
            )

            Text(
                text = stringResource(level.titleResId),
                color = onSelectedColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun BasicStatsUi(viewModel: HistoryViewModel = hiltViewModel()) {
    val noGamesYet = viewModel.labelNoGamesYet

    val totalGamesLabel = viewModel.labelTotalGames
    val totalGames by viewModel.totalGamesSaved.collectAsState()

    val totalTodayLabel = viewModel.labelTotalGamesToday
    val totalToday by viewModel.totalGamesToday.collectAsState()

    val totalTimeLabel = viewModel.labelTotalTime
    val totalTime by viewModel.totalPlayTime.collectAsState()

    val firstDateLabel = viewModel.labelFirstGame
    val firstDate by viewModel.firstGameDate.collectAsState()

    if (totalGames == 0) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) { Text(text = noGamesYet) }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PaddingDefault, Alignment.CenterVertically)
        ) {
            Text(
                text = stringResource(R.string.you_games_statistics),
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )

            TableRow(
                key = totalGamesLabel,
                value = "$totalGames"
            )
            TableRow(
                key = totalTodayLabel,
                value = "$totalToday"
            )
            TableRow(
                key = totalTimeLabel,
                value = totalTime.toFormattedTime(false)
            )
            TableRow(
                key = firstDateLabel,
                value = firstDate?.formatDateTime() ?: "-"
            )
        }
    }
}

@Composable
private fun ColumnScope.TableRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PaddingDefault, vertical = PaddingMini),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = key, fontWeight = FontWeight.Bold)
        Text(text = value, fontWeight = FontWeight.Bold)
    }
}