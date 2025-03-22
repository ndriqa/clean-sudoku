package com.ndriqa.cleansudoku.core.sounds

import android.content.Context
import android.media.SoundPool
import androidx.annotation.StringRes
import com.ndriqa.cleansudoku.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundEffectManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val soundPool = SoundPool.Builder().setMaxStreams(4).build()

    private val clickSoundId = soundPool.load(context, R.raw.click_002, 1)
    private val switchSoundId = soundPool.load(context, R.raw.switch_005, 1)
    private val selectSoundId = soundPool.load(context, R.raw.select_008, 1)

    fun playClick() = soundPool.playSimple(clickSoundId)
    fun playSelect() = soundPool.playSimple(selectSoundId)
    fun playSwitch() = soundPool.playSimple(switchSoundId)

    fun release() {
        soundPool.release()
    }

    private fun SoundPool.playSimple(soundId: Int) = play(soundId, 1f, 1f, 0, 0, 1f)
}
