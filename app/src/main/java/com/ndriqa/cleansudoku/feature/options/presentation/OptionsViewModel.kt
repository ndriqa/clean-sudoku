package com.ndriqa.cleansudoku.feature.options.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndriqa.cleansudoku.R
import com.ndriqa.cleansudoku.core.data.AnalyticsEvent
import com.ndriqa.cleansudoku.core.domain.preferences.DataStoreManager
import com.ndriqa.cleansudoku.core.util.extensions.bzz
import com.ndriqa.cleansudoku.core.util.extensions.getVibrator
import com.ndriqa.cleansudoku.core.util.extensions.logEvent
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    private val vibrator = context.getVibrator()

    val preferredDifficulty = dataStoreManager.preferredDifficulty
        .stateIn(viewModelScope, SharingStarted.Lazily, Level.EASY)

    val soundEnabled = dataStoreManager.enableSound
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val vibrationEnabled = dataStoreManager.enableVibration
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun selectPreferredLevel(level: Level) {
        viewModelScope.launch {
            dataStoreManager.setPreferredDifficulty(level)

            AnalyticsEvent.Switch(
                switchName = "level_difficulty",
                optionSelected = level.name
            ).logEvent()
        }
    }

    fun toggleSound() {
        viewModelScope.launch {
            dataStoreManager.toggleSound()

            val enabled = dataStoreManager.enableSound.first()

            AnalyticsEvent.Switch(
                switchName = "sound",
                optionSelected = "$enabled"
            ).logEvent()
        }
    }

    fun toggleVibration() {
        viewModelScope.launch {
            dataStoreManager.toggleVibration()

            val enabled = dataStoreManager.enableVibration.first()
            if (enabled) vibrator?.bzz()

            AnalyticsEvent.Switch(
                switchName = "vibration",
                optionSelected = "$enabled"
            ).logEvent()
        }
    }
}