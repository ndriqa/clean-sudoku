package com.ndriqa.cleansudoku.feature.options.presentation

import android.graphics.drawable.shapes.RoundRectShape
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ElectricBolt
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.OfflineBolt
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ndriqa.cleansudoku.R
import com.ndriqa.cleansudoku.core.util.extensions.getVibrator
import com.ndriqa.cleansudoku.core.util.extensions.vibratePattern
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.ui.components.SplitScreen
import com.ndriqa.cleansudoku.ui.theme.CardSize
import com.ndriqa.cleansudoku.ui.theme.CardSizeBig
import com.ndriqa.cleansudoku.ui.theme.PaddingCompact
import com.ndriqa.cleansudoku.ui.theme.PaddingDefault
import com.ndriqa.cleansudoku.ui.theme.PaddingHalf
import com.ndriqa.cleansudoku.ui.theme.TopBarSize

@Composable
fun OptionsScreen(
    navController: NavController,
    viewModel: OptionsViewModel = hiltViewModel()
) {

    val selectedLevel by viewModel.preferredDifficulty.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()

    LaunchedEffect(soundEnabled) {

    }

//    LaunchedEffect(vibrationEnabled) {
//        vibrator?.let {
//            val vibrationPattern = longArrayOf(0, 40, 100, 120) // delay, vibrate, delay, vibrate
//            if (vibrationEnabled) it.vibratePattern(pattern = vibrationPattern)
//        }
//    }

    Scaffold(
        topBar = { TopBarUi(onBackPress = navController::navigateUp) },
        containerColor = Color.Transparent
    ) { paddingValues ->

        SplitScreen(
            modifier = Modifier.padding(paddingValues),
            primaryContent = {
                LevelSelectorUi(
                    currentSelectedLevel = selectedLevel,
                    onLevelClicked = viewModel::selectPreferredLevel
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
            SoundCard(
                enabled = soundEnabled,
                onToggle = onSoundToggle
            )
            VibrationCard(
                enabled = vibrationEnabled,
                onToggle = onVibrationToggle
            )
        }
    }
}

@Composable
fun RowScope.SoundCard(
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

    Column(
        modifier = modifier
            .weight(1F)
            .height(CardSizeBig)
            .clip(RoundedCornerShape(PaddingDefault))
            .clickable(onClick = onToggle)
            .background(color = backgroundColor)
            .padding(PaddingDefault),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val icon =
            if (enabled) Icons.AutoMirrored.Rounded.VolumeUp
            else Icons.AutoMirrored.Rounded.VolumeOff

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
        Text(
            text = stringResource(R.string.sound),
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RowScope.VibrationCard(
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

    Column(
        modifier = modifier
            .weight(1F)
            .height(CardSizeBig)
            .clip(RoundedCornerShape(PaddingDefault))
            .clickable(onClick = onToggle)
            .background(color = backgroundColor)
            .padding(PaddingDefault),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val icon =
            if (enabled) Icons.Rounded.Vibration
            else Icons.Rounded.PhoneAndroid

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )
        Text(
            text = stringResource(R.string.vibration),
            color = contentColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LevelSelectorUi(
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
fun RowScope.LevelItemUi(
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

private fun Level.getMaterialIcon() = when(this) {
    Level.EASY -> Icons.Rounded.CheckCircle
    Level.MID -> Icons.Rounded.Extension
    Level.HARD -> Icons.Rounded.ElectricBolt
}

@Composable
private fun TopBarUi(onBackPress: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(TopBarSize),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onBackPress, modifier = Modifier.size(TopBarSize)) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = stringResource(R.string.cd_back_button),
            )
        }
    }
}