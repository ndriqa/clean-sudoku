package com.ndriqa.cleansudoku.feature.options.presentation

import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ndriqa.cleansudoku.R
import com.ndriqa.cleansudoku.core.util.extensions.getMaterialIcon
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.feature.sounds.presentation.SoundsViewModel
import com.ndriqa.cleansudoku.ui.components.SplitScreen
import com.ndriqa.cleansudoku.ui.components.TopBarUi
import com.ndriqa.cleansudoku.ui.theme.CardSize
import com.ndriqa.cleansudoku.ui.theme.CardSizeBig
import com.ndriqa.cleansudoku.ui.theme.PaddingCompact
import com.ndriqa.cleansudoku.ui.theme.PaddingDefault

@Composable
fun OptionsScreen(
    navController: NavController,
    viewModel: OptionsViewModel = hiltViewModel(),
    soundsViewModel: SoundsViewModel = hiltViewModel()
) {
    val selectedLevel by viewModel.preferredDifficulty.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()

    Scaffold(
        topBar = { TopBarUi(onBackPress = navController::navigateUp) },
        containerColor = Color.Transparent
    ) { paddingValues ->

        fun onSelectedLevel(level: Level) {
            if (soundEnabled) soundsViewModel.switch()
            viewModel.selectPreferredLevel(level)
        }

        SplitScreen(
            modifier = Modifier.padding(paddingValues),
            primaryContent = {
                LevelSelectorUi(
                    currentSelectedLevel = selectedLevel,
                    onLevelClicked = ::onSelectedLevel
                )
            },
            secondaryContent = {
                MiscSettingsUi(
                    soundEnabled = soundEnabled,
                    vibrationEnabled = vibrationEnabled,
                    onSoundToggle = viewModel::toggleSound,
                    onVibrationToggle = viewModel::toggleVibration
                )
            }
        )
    }
}

@Composable
fun MiscSettingsUi(
    soundEnabled: Boolean,
    vibrationEnabled: Boolean,
    onSoundToggle: () -> Unit,
    onVibrationToggle: () -> Unit,
) {
    val soundIcon =
        if (soundEnabled) Icons.AutoMirrored.Rounded.VolumeUp
        else Icons.AutoMirrored.Rounded.VolumeOff

    val vibrationIcon =
        if (vibrationEnabled) Icons.Rounded.Vibration
        else Icons.Rounded.PhoneAndroid

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingDefault, alignment = Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(R.string.sound_vibration),
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier.fillMaxWidth(.75f),
            horizontalArrangement = Arrangement.spacedBy(PaddingDefault)
        ) {
            ToggleCard(
                titleResId = R.string.sound,
                icon = soundIcon,
                enabled = soundEnabled,
                onToggle = onSoundToggle
            )

            ToggleCard(
                titleResId = R.string.vibration,
                icon = vibrationIcon,
                enabled = vibrationEnabled,
                onToggle = onVibrationToggle
            )
        }
    }
}

@Composable
fun RowScope.ToggleCard(
    @StringRes titleResId: Int,
    icon: ImageVector,
    enabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (enabled) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onPrimary
    val contentColor =
        if (enabled.not()) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.onPrimary

    val selectedBackgroundColor by animateColorAsState(
        targetValue = backgroundColor,
        label = "ToggleCardBackgroundAnimation",
    )
    val onSelectedBackgroundColor by animateColorAsState(
        targetValue = contentColor,
        label = "ToggleCardBackgroundAnimation"
    )

    Column(
        modifier = modifier
            .weight(1F)
            .height(CardSizeBig)
            .clip(RoundedCornerShape(PaddingDefault))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(PaddingDefault)
            )
            .clickable(onClick = onToggle)
            .background(color = selectedBackgroundColor)
            .padding(PaddingDefault),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = onSelectedBackgroundColor
        )

        Text(
            text = stringResource(titleResId),
            color = onSelectedBackgroundColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LevelSelectorUi(
    currentSelectedLevel: Level,
    onLevelClicked: (Level) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PaddingDefault, alignment = Alignment.CenterVertically)
    ) {
        Text(
            text = stringResource(R.string.level_difficulty),
            color = MaterialTheme.colorScheme.primary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(CardSize)
                .clip(RoundedCornerShape(PaddingDefault))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(PaddingDefault)
                )
        ) {
            Level.entries.map { LevelItemUi(
                level = it,
                selected = currentSelectedLevel == it,
                onLevelClicked = onLevelClicked
            ) }
        }
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

    val selectedBackgroundColor by animateColorAsState(
        targetValue = selectedColor,
        label = "LevelBackgroundAnimation",
    )

    val onSelectedBackgroundColor by animateColorAsState(
        targetValue = onSelectedColor,
        label = "LevelBackgroundAnimation"
    )

    val scaleFactor by animateFloatAsState(
        targetValue = if (selected) 1.25f else 1f,
        label = "LevelScaleAnimation",
    )

    Box(
        modifier = modifier
            .weight(scaleFactor)
            .background(color = selectedBackgroundColor)
            .clickable(onClick = { onLevelClicked(level) })
            .padding(PaddingDefault)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(PaddingCompact, alignment = Alignment.CenterVertically)
        ) {
            Icon(
                imageVector = level.getMaterialIcon(),
                contentDescription = stringResource(level.titleResId),
                tint = onSelectedBackgroundColor
            )

            Text(
                text = stringResource(level.titleResId),
                color = onSelectedBackgroundColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}