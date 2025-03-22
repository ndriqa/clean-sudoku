package com.ndriqa.cleansudoku.feature.sounds.presentation

import androidx.lifecycle.ViewModel
import com.ndriqa.cleansudoku.core.sounds.BackgroundSoundManager
import com.ndriqa.cleansudoku.core.sounds.SoundEffectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SoundsViewModel @Inject constructor(
    private val backgroundSoundManager: BackgroundSoundManager,
    private val soundEffectManager: SoundEffectManager
): ViewModel() {
    fun startBackgroundMusic() {
        backgroundSoundManager.startBackgroundMusic()
    }

    fun pauseBackgroundMusic() {
        backgroundSoundManager.pauseBackgroundMusic()
    }

    fun click() {
        soundEffectManager.playClick()
    }

    fun switch() {
        soundEffectManager.playSwitch()
    }

    fun select() {
        soundEffectManager.playSelect()
    }

    fun releaseMediaPlayers() {
        backgroundSoundManager.stopAndRelease()
        soundEffectManager.release()
    }
}