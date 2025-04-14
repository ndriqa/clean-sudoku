package com.ndriqa.cleansudoku.feature.home.presentation

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import com.ndriqa.cleansudoku.BuildConfig
import com.ndriqa.cleansudoku.core.data.AnalyticsEvent
import com.ndriqa.cleansudoku.core.data.CompletedGame
import com.ndriqa.cleansudoku.core.data.SudokuBoard
import com.ndriqa.cleansudoku.core.domain.preferences.DataStoreManager
import com.ndriqa.cleansudoku.core.util.extensions.logEvent
import com.ndriqa.cleansudoku.core.util.extensions.toAnalyticsString
import com.ndriqa.cleansudoku.core.util.sudoku.Level
import com.ndriqa.cleansudoku.core.util.sudoku.generateSudoku
import com.ndriqa.cleansudoku.data.repository.CompletedGameRepository
import com.ndriqa.cleansudoku.ui.data.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val dataStoreManager: DataStoreManager,
    private val completedGameRepository: CompletedGameRepository
): ViewModel() {
    private val _sudoku : MutableStateFlow<UiState<SudokuBoard>> = MutableStateFlow(UiState.Idle)
    val sudoku = _sudoku.asStateFlow()

    val preferredDifficulty = dataStoreManager.preferredDifficulty
        .stateIn(viewModelScope, SharingStarted.Lazily, Level.EASY)

    val lastReviewedVersion = dataStoreManager.reviewedVersion
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    private val _completedGames: StateFlow<List<CompletedGame>> = completedGameRepository.getAllGames()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _completedGamesNumber = _completedGames
        .map { it.size }
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    val showInAppReviewDialog = combine(
        _completedGamesNumber,
        lastReviewedVersion
    ) { games, reviewedVersion ->
        val currentVersionCode = BuildConfig.VERSION_CODE
        val hasAlreadyReviewed = reviewedVersion == currentVersionCode
        val hasEligibleNumOfGames = (games > 0) && (games % 3 == 0)

        Timber.tag("Play Store Review").d("Current app version code: $currentVersionCode")
        Timber.tag("Play Store Review").d("Last Reviewed app version code: $reviewedVersion")

        !hasAlreadyReviewed && hasEligibleNumOfGames
    }.shareIn(viewModelScope, SharingStarted.Eagerly)

    fun tryGenerateSudoku() {
        _sudoku.value = UiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val sudokuBoard = generateSudoku()
            val endTime = System.currentTimeMillis()
            withContext(Dispatchers.Main) {
                _sudoku.value = UiState.Success(SudokuBoard(sudokuBoard))
            }
            logSudokuGeneratedEvent(
                startTime = startTime,
                endTime = endTime,
                sudokuBoard = sudokuBoard
            )
        }
    }

    fun logSudokuGeneratedEvent(
        startTime: Long,
        endTime: Long,
        sudokuBoard: Array<IntArray>
    ) {
        val duration = endTime - startTime
        val sudokuBoardText = sudokuBoard.toAnalyticsString()
        AnalyticsEvent.CustomEvent(
            eventName = "sudoku_generation",
            customParams = mapOf(
                "start_time" to startTime,
                "end_time" to endTime,
                "duration" to duration,
                "generated_sudoku_board" to sudokuBoardText
            )
        ).logEvent()
    }

    fun resetGeneratedSudoku() {
        _sudoku.value = UiState.Idle
    }

    fun launchInAppReviewDialog(activity: Activity) {
        val reviewManager =
            if (BuildConfig.DEBUG) FakeReviewManager(context)
            else ReviewManagerFactory.create(context)
        val request = reviewManager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                val flow = reviewManager.launchReviewFlow(activity, reviewInfo)
                flow.addOnCompleteListener { _ ->
                    viewModelScope.launch {
                        dataStoreManager.updateReviewedVersionCode(BuildConfig.VERSION_CODE)
                        AnalyticsEvent.CustomEvent(eventName = "play_store_review").logEvent()
                    }
                }
            } else {
                @ReviewErrorCode val reviewErrorCode =
                    (task.exception as? ReviewException)?.errorCode
            }
        }
    }
}