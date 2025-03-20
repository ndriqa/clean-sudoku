package com.ndriqa.cleansudoku.core.domain.preferences

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class DataStoreManager @Inject constructor(private val context: Context) {

    private val KeyExamplePref = stringPreferencesKey(STRING_KEY_EXAMPLE_PREF)
    private val KeyPreferredDifficulty = stringPreferencesKey(STRING_KEY_EXAMPLE_PREF)

    val preferredDifficulty: Flow<Level> = context.dataStore.data
        .map { preferences ->
            val savedDifficultyRes = preferences[KeyPreferredDifficulty] ?: Level.EASY.id
            return@map Level.getLevel(savedDifficultyRes)
        }

    suspend fun setPreferredDifficulty(level: Level) {
        context.dataStore.edit { preferences ->
            preferences[KeyPreferredDifficulty] = level.id
        }
    }

    suspend fun changePrefSuspend(newData: String) {
        context.dataStore.edit { preferences ->
            preferences[KeyExamplePref] = newData
        }
    }

    companion object {
        private const val STRING_KEY_EXAMPLE_PREF = "STRING_KEY_EXAMPLE_PREF"
        private const val STRING_KEY_PREFERRED_DIFFICULTY = "STRING_KEY_PREFERRED_DIFFICULTY"
    }
}