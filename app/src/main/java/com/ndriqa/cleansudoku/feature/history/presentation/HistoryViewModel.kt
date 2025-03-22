package com.ndriqa.cleansudoku.feature.history.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndriqa.cleansudoku.R
import com.ndriqa.cleansudoku.core.data.CompletedGame
import com.ndriqa.cleansudoku.core.domain.preferences.DataStoreManager
import com.ndriqa.cleansudoku.core.util.extensions.formatDateTime
import com.ndriqa.cleansudoku.core.util.extensions.toFormattedTime
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.data.repository.CompletedGameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.collections.count

@HiltViewModel
class HistoryViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: CompletedGameRepository,
    private val dataStoreManager: DataStoreManager
): ViewModel() {
    val labelNoGamesYet = context.getString(R.string.label_no_games_yet)
    val labelTotalGames = context.getString(R.string.label_total_games)
    val labelTotalGamesToday = context.getString(R.string.label_total_games_today)
    val labelAverageTime = context.getString(R.string.label_average_time)
    val labelTotalTime = context.getString(R.string.label_total_time)
    val labelFastestTime = context.getString(R.string.label_fastest_time)
    val labelLastGame = context.getString(R.string.label_last_game)
    val labelFirstGame = context.getString(R.string.label_first_game)

    sealed class LevelSelectionState {
        object Loading : LevelSelectionState()
        data class Ready(val level: Level) : LevelSelectionState()
    }

    val selectedLevelState = MutableStateFlow<LevelSelectionState>(LevelSelectionState.Loading)

    val savedGames: StateFlow<List<CompletedGame>> = repository.getAllGames()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val easyGames = savedGames
        .map { it.filter { it.difficulty == Level.EASY } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val midGames = savedGames
        .map { it.filter { it.difficulty == Level.MID } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())
    private val hardGames = savedGames
        .map { it.filter { it.difficulty == Level.HARD } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val easyStats = easyGames
        .map { it.getBasicStats() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
    val midStats = midGames
        .map { it.getBasicStats() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())
    val hardStats = hardGames
        .map { it.getBasicStats() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    init {
        viewModelScope.launch {
            val preferred = dataStoreManager.preferredDifficulty.firstOrNull() ?: Level.EASY
            updateSelectedLevel(preferred)
        }
    }

    val selectedStats = combine(
        selectedLevelState,
        easyStats,
        midStats,
        hardStats
    ) { selection, easy, mid, hard ->
        when (selection) {
            is LevelSelectionState.Ready -> when (selection.level) {
                Level.EASY -> easy
                Level.MID -> mid
                Level.HARD -> hard
            }
            LevelSelectionState.Loading -> emptyMap()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyMap())

    val totalGamesSaved = savedGames
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0)

    val totalPlayTime = savedGames
        .map { it.sumOf { game -> game.completionTime } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), 0L)

    val firstGameDate = savedGames
        .map { it.minByOrNull { game -> game.completedDate.toEpochSecond() }?.completedDate }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val totalGamesToday = savedGames
        .map { it.count { it.completedDate.toLocalDate() == ZonedDateTime.now().toLocalDate() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun updateSelectedLevel(level: Level) {
        selectedLevelState.value = LevelSelectionState.Ready(level)
    }

    private fun List<CompletedGame>.getBasicStats(): Map<String, String> {
        val stats = mutableMapOf<String, String>()
        if (isEmpty()) return stats

        val totalGames = size
        val today = ZonedDateTime.now().toLocalDate()
        val gamesToday = count { it.completedDate.toLocalDate() == today }

        val avgTime = map { it.completionTime }.average().toLong()
        val totalTime = sumOf { it.completionTime }
        val fastest = minOfOrNull { it.completionTime } ?: 0L

        val lastGameDate = maxByOrNull { it.completedDate.toEpochSecond() }?.completedDate
        val firstGameDate = minByOrNull { it.completedDate.toEpochSecond() }?.completedDate

        stats[labelTotalGames] = totalGames.toString()
        stats[labelTotalGamesToday] = gamesToday.toString()
        stats[labelAverageTime] = avgTime.toFormattedTime(false)
        stats[labelTotalTime] = totalTime.toFormattedTime(false)
        stats[labelFastestTime] = fastest.toFormattedTime(false)
        stats[labelLastGame] = lastGameDate?.formatDateTime() ?: "-"
        stats[labelFirstGame] = firstGameDate?.formatDateTime() ?: "-"

        return stats
    }
}