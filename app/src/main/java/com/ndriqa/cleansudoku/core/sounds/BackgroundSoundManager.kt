package com.ndriqa.cleansudoku.core.sounds

import android.content.Context
import android.media.MediaPlayer
import com.ndriqa.cleansudoku.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackgroundSoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null

    fun startBackgroundMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.alpha_dance).apply {
                isLooping = true
                setVolume(0.1f, 0.1f)
                start()
            }
        } else {
            mediaPlayer?.start()
        }
    }

    fun pauseBackgroundMusic() {
        mediaPlayer?.pause()
    }

    fun stopAndRelease() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}